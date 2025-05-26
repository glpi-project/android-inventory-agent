package org.glpi.inventory.agent.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.HttpInventory;
import org.glpi.inventory.agent.utils.LocalPreferences;

import java.util.ArrayList;

public class InventoryAgent extends BroadcastReceiver {
    public InventoryAgent() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final InventoryTask inventory = new InventoryTask(context.getApplicationContext(), Helpers.getAgentDescription(context), true);
        final HttpInventory httpInventory = new HttpInventory(context.getApplicationContext());
        ArrayList<String> serverArray = new LocalPreferences(context).loadServer();
        if (!serverArray.isEmpty()) {
            for (final String serverName : serverArray) {
                final ServerSchema model = httpInventory.setServerModel(serverName);
                inventory.setTag(model.getTag());
                inventory.setAssetItemtype(model.getItemtype());
                inventory.getXML(new InventoryTask.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        ServerSchema model = httpInventory.setServerModel(serverName);
                        if (!model.getSerial().trim().isEmpty()) {
                            data = data.replaceAll("<SSN>(.*)</SSN>", "<SSN>" + model.getSerial() + "</SSN>");
                        }
                        httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                            @Override
                            public void onTaskSuccess(String data) {
                                //Helpers.sendAnonymousData(context.getApplicationContext(), inventory);
                            }

                            @Override
                            public void onTaskError(String error) {
                                AgentLog.e(error);
                            }
                        });
                    }

                    @Override
                    public void onTaskError(Throwable error) {
                        AgentLog.e(error.getMessage());
                    }
                });
            }
        } else {
            AgentLog.e(context.getResources().getString(R.string.inventory_no_server));
        }
    }
}
