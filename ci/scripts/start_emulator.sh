DEFAULT_API=23

# create package
sdkmanager "system-images;android-$DEFAULT_API;google_apis;x86"

# Launch emulator
echo no | avdmanager create avd -n test -k "system-images;android-$DEFAULT_API;google_apis;x86" -b x86 -c 100M -d 7 -f

