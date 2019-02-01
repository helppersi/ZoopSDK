package br.com.helpper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Vector;

public class ZoopPlugin extends CordovaPlugin implements DeviceSelectionListener {

    TerminalListManager terminalListManager;

    CallbackContext listenerShowDeviceListForUserSelection;
    CallbackContext listenerUpdateDeviceListForUserSelecion;
    CallbackContext listenerBluetoothIsNotEnabledNotification;
    CallbackContext listenerDeviceSelectedResult;
    CallbackContext listenerLogs;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        try {
            ZoopAPI.initialize(cordova.getActivity().getApplication());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            terminalListManager.finishTerminalDiscovery();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void showDeviceListForUserSelection(final Vector<JSONObject> vectorZoopTerminals) {
        try {
            if(this.listenerShowDeviceListForUserSelection) {
                JSONArray array = new JSONArray();
                for(JSONObject object : vectorZoopTerminals) {
                    array.put(object);
                }

                this.log("Devices: " + vectorZoopTerminals.size());
                this.listenerShowDeviceListForUserSelection.success(array);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerShowDeviceListForUserSelection);
        }
    }

    @Override
    public void updateDeviceListForUserSelecion(JSONObject newFoundZoopDevice, Vector<JSONObject> vectorZoopTerminals, int index) {
        try {
            if(this.listenerUpdateDeviceListForUserSelecion) {
                JSONObject data = new JSONObject();
                data.put("device", newFoundZoopDevice);
                data.put("deviceIndex", index);

                JSONArray array = new JSONArray();
                for(JSONObject object : vectorZoopTerminals) {
                    array.put(object);
                }
                data.put("zoopTerminals", array);

                this.log("New Device: " + newFoundZoopDevice.getString("name"));
                this.listenerUpdateDeviceListForUserSelecion.success(data);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerUpdateDeviceListForUserSelecion);
        }
    }

    @Override
    public void bluetoothIsNotEnabledNotification() {
        this.log("Bluetooth Is Not Enable");
        try {
            if(this.listenerBluetoothIsNotEnabledNotification) {
                this.listenerBluetoothIsNotEnabledNotification.success();
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerBluetoothIsNotEnabledNotification);
        }
    }

    @Override
    public void deviceSelectedResult(JSONObject selectedDevice, Vector<JSONObject> allAvailableZoopTerminals, int index) {
        try {
            this.log("Select Device: " + selectedDevice.getString("name"));

            if(this.listenerDeviceSelectedResult) {
                JSONObject data = new JSONObject();
                data.put("device", selectedDevice);
                data.put("deviceIndex", index);

                JSONArray array = new JSONArray();
                for(JSONObject object : allAvailableZoopTerminals) {
                    array.put(object);
                }
                data.put("allAvailableZoopTerminals", array);

                this.listenerDeviceSelectedResult.success(data);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerDeviceSelectedResult);
        }
    }

    public void log(String message) {
        if(this.listenerLogs) {
            this.listenerLogs.success(message);
        }
    }

    private void error(Exception exception, CallbackContext listener) {
        this.log("Erro: " + exception.getMessage());
        exception.printStackTrace();
        if(listener) {
            listener.error(exception.getMessage());
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if(action.equals("startDiscovery")) {
            terminalListManager.startTerminalsDiscovery();
            terminalListManager = new TerminalListManager(this, this.cordova.getActivity().getApplicationContext());
            this.log("Start Discovery");
            return true;
        }

        else if(action.equals("enableDeviceBluetoothAdapter")) {
            terminalListManager.enableDeviceBluetoothAdapter();
            this.log("Enable Device BluetoothAdapter");
            return true;
        }

        else if(action.equals("requestZoopDeviceSelection")) {
            // Get variable
            final String device = args.getString(0);
            terminalListManager.requestZoopDeviceSelection(device);
            return true;
        }

        else if(action.equals("finishDiscovery")) {
            terminalListManager.finishTerminalDiscovery();
            this.listenerShowDeviceListForUserSelection = null;
            this.listenerUpdateDeviceListForUserSelecion = null;
            this.listenerBluetoothIsNotEnabledNotification = null;
            this.listenerDeviceSelectedResult = null;
            return true;
        }


        // Listeners Device

        else if(action.equals("showDeviceListForUserSelection")) {
            this.listenerShowDeviceListForUserSelection = callbackContext;
            return true;
        }

        else if(action.equals("updateDeviceListForUserSelecion")) {
            this.listenerUpdateDeviceListForUserSelecion = callbackContext;
            return true;
        }

        else if(action.equals("bluetoothIsNotEnabledNotification")) {
            this.listenerBluetoothIsNotEnabledNotification = callbackContext;
            return true;
        }

        else if(action.equals("deviceSelectedResult")) {
            this.listenerDeviceSelectedResult = callbackContext;
            return true;
        }

        else if(action.equals("logsObserver")) {
            this.listenerLogs = callbackContext;
            return true;
        }

        else
            return false;

    }
}
