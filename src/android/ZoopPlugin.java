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
        terminalListManager.finishTerminalDiscovery();
        super.onDestroy();
    }

    @Override
    public void showDeviceListForUserSelection(final Vector<JSONObject> vectorZoopTerminals) {
        try {
            if(vectorZoopTerminals.size() > 0) {
                cordova.getActivity().runOnUiThread(new Runnable(){
                    public void run() {
                        android.widget.Toast toast = android.widget.Toast.makeText(
                                cordova.getActivity().getApplicationContext(),
                                vectorZoopTerminals.get(0).toString(),
                                android.widget.Toast.LENGTH_LONG
                        );

                        toast.show();
                    }
                });
                //terminalListManager.requestZoopDeviceSelection(vectorZoopTerminals.get(0));
            }
            else {
                cordova.getActivity().runOnUiThread(new Runnable(){
                    public void run() {
                        android.widget.Toast toast = android.widget.Toast.makeText(
                                cordova.getActivity().getApplicationContext(),
                                "Nenhum dispositivo encontrado",
                                android.widget.Toast.LENGTH_LONG
                        );

                        toast.show();
                    }
                });
            }
        }
        catch (Exception e) {
            ZLog.exception(300064, e);
        }
    }

    @Override
    public void updateDeviceListForUserSelecion(JSONObject joNewlyFoundZoopDevice, Vector<JSONObject> vectorZoopTerminals, int iNewlyFoundDeviceIndex) {
        try {
            //terminalListManager.requestZoopDeviceSelection(joNewlyFoundZoopDevice);
            final JSONObject device = joNewlyFoundZoopDevice;
            this.cordova.getActivity().runOnUiThread(new Runnable(){
                public void run() {
                    android.widget.Toast toast = android.widget.Toast.makeText(
                            cordova.getActivity().getApplicationContext(),
                            device.toString(),
                            android.widget.Toast.LENGTH_LONG
                    );
                    toast.show();
                }
            });
        } catch (Exception e) {
            ZLog.exception(677541, e);
        }
    }

    @Override
    public void bluetoothIsNotEnabledNotification() {
        this.cordova.getActivity().runOnUiThread(new Runnable(){
            public void run() {
                android.widget.Toast toast = android.widget.Toast.makeText(
                        cordova.getActivity().getApplicationContext(),
                        "BLUETOOTH desabilidade",
                        android.widget.Toast.LENGTH_LONG
                );

                //toast.show();
            }
        });
        terminalListManager.enableDeviceBluetoothAdapter();
    }

    @Override
    public void deviceSelectedResult(JSONObject joZoopSelectedDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iSelectedDeviceIndex) {
        //String namePinpad = joZoopSelectedDevice.getString("name");
    }

    public void stopDiscovery(){
        terminalListManager.finishTerminalDiscovery();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackCtx) throws JSONException{

        if(action.equals("exibe")) {

            terminalListManager = new TerminalListManager(this, this.cordova.getActivity().getApplicationContext());
            terminalListManager.startTerminalsDiscovery();

            //final String dados = args.getString(0);
            //final String mensagem = "Sua mensagem: "+dados;

            /*
            this.cordova.getActivity().runOnUiThread(new Runnable(){
                public void run() {
                    android.widget.Toast toast = android.widget.Toast.makeText(
                            this.cordova.getActivity().getApplicationContext(),
                            mensagem,
                            android.widget.Toast.LENGTH_LONG
                    );

                    toast.show();

                    callbackCtx.success();
                }
            });
            */

            return true;

        }
        else
            return false;

    }
}
