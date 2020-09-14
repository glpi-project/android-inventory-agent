package org.glpi.inventory.agent.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.flyve.inventory.InventoryLog;
import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.HttpInventory;
import org.glpi.inventory.agent.utils.LocalPreferences;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SendInventoryWorker extends Worker {

    private ArrayList<String> msgError = new ArrayList<>();
    private boolean errorOnSendInventory;
    private boolean errorOnInventory;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public SendInventoryWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        InventoryLog.d("WORKER -> start inventory");

        errorOnInventory = false;
        errorOnSendInventory = false;

        final Context context = getApplicationContext();
        final InventoryTask inventory = new InventoryTask(context.getApplicationContext(), Helpers.getAgentDescription(context), true);
        final HttpInventory httpInventory = new HttpInventory(context.getApplicationContext());
        final ArrayList<String> serverArray = new LocalPreferences(context).loadServer();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        //if server are stored
        if (!serverArray.isEmpty()) {
            //first step try to load inventory
            inventory.getXML(new InventoryTask.OnTaskCompleted() {
                @Override
                public void onTaskSuccess(String data) {

                    InventoryLog.d("WORKER -> Inventory completed");
                    for (String serverName : serverArray) {
                        final ServerSchema model = httpInventory.setServerModel(serverName);
                        httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                            @Override
                            public void onTaskSuccess(String data) {
                                InventoryLog.d("WORKER -> Inventory send");
                                countDownLatch.countDown();
                            }

                            @Override
                            public void onTaskError(String error) {
                                InventoryLog.d("WORKER -> Inventory not send");
                                InventoryLog.d("WORKER -> error : " + error);
                                msgError.add(error);
                                errorOnSendInventory = true;
                                countDownLatch.countDown();
                            }
                        });
                    }
                }

                @Override
                public void onTaskError(Throwable error) {
                    InventoryLog.d("WORKER -> Inventory error");
                    InventoryLog.d("WORKER -> error : " + error.getMessage());
                    AgentLog.e(error.getMessage());
                    Helpers.sendToNotificationBar(context, context.getResources().getString(R.string.inventory_notification_fail));
                    errorOnInventory = true;
                    countDownLatch.countDown();
                }
            });
        }else{
            InventoryLog.d("WORKER -> Inventory no server");
            Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.no_servers_added));
            errorOnInventory =true;
            countDownLatch.countDown();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Result taskResult;
        if(errorOnInventory){
            InventoryLog.d("WORKER -> failure");
            taskResult =  Result.failure();
        }else{
            if(errorOnSendInventory){

                String[] mStringArray = new String[msgError.size()];
                mStringArray = msgError.toArray(mStringArray);

                for (String s : mStringArray) {
                    Helpers.sendToNotificationBar(context.getApplicationContext(), s);
                }
                InventoryLog.d("WORKER -> need to be retry");
                taskResult = Result.retry();
            }else{
                InventoryLog.d("WORKER -> is success");
                Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.inventory_notification_sent));
                taskResult = Result.success();
            }
        }

        return taskResult;

    }
}
