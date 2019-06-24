package br.com.helpper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.terminal.TerminalMessageType;
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;
import com.zoop.zoopandroidsdk.terminal.TerminalPaymentListener;
import com.zoop.zoopandroidsdk.terminal.ApplicationDisplayListener;
import com.zoop.zoopandroidsdk.ZoopTerminalPayment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Vector;


public class ZoopPlugin extends CordovaPlugin implements DeviceSelectionListener, TerminalPaymentListener, ApplicationDisplayListener {

    TerminalListManager terminalListManager;

    CallbackContext listenerShowDeviceListForUserSelection;
    CallbackContext listenerUpdateDeviceListForUserSelecion;
    CallbackContext listenerBluetoothIsNotEnabledNotification;
    CallbackContext listenerDeviceSelectedResult;
    CallbackContext listenerLogs;

    // Payment
    CallbackContext listenerPayment;
    CallbackContext listenerCardholderSignatureRequested;
    CallbackContext listenerCurrentChargeCanBeAbortedByUser;
    CallbackContext listenerSignatureResult;
    CallbackContext listenerShowMessage;

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
        this.log("Devices: " + vectorZoopTerminals.size());
        try {
            if(this.listenerShowDeviceListForUserSelection != null) {
                JSONArray array = new JSONArray();
                for(JSONObject object : vectorZoopTerminals) {
                    array.put(object);
                }

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
            this.log("New Device: " + newFoundZoopDevice.getString("name"));

            if(this.listenerUpdateDeviceListForUserSelecion != null) {
                JSONObject data = new JSONObject();
                data.put("device", newFoundZoopDevice);
                data.put("deviceIndex", index);

                JSONArray array = new JSONArray();
                for(JSONObject object : vectorZoopTerminals) {
                    array.put(object);
                }
                data.put("zoopTerminals", array);

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
            if(this.listenerBluetoothIsNotEnabledNotification != null) {
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

            if(this.listenerDeviceSelectedResult != null) {
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
        if(this.listenerLogs != null) {
            this.listenerLogs.success(message);
        }
    }

    private void error(Exception exception, CallbackContext listener) {
        this.log("Erro: " + exception.getMessage());
        exception.printStackTrace();
        if(listener != null) {
            listener.error(exception.getMessage());
        }
    }


    /*
     *
     *  Payment listeners
     *
     *
     */

    public void paymentSendListener(JSONObject data) {
        try {
            if(this.listenerPayment != null) {
                this.listenerPayment.success(data);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerPayment);
        }
    }

    @Override
    public void paymentSuccessful(JSONObject jsonObject) {
        this.log("Payment Successful: " + jsonObject.toString());

        try {
            JSONObject data = new JSONObject();
            data.put("success", true);
            data.put("data", jsonObject);
            this.paymentSendListener(data);
        }
        catch (Exception e) {
            this.log("Erro: " + e.getMessage());
        }
    }

    @Override
    public void paymentFailed(JSONObject jsonObject) {
        this.log("Payment Failed: " + jsonObject.toString());

        try {
            JSONObject data = new JSONObject();
            data.put("success", false);
            data.put("data", jsonObject);
            this.paymentSendListener(data);
        }
        catch (Exception e) {
            this.log("Erro: " + e.getMessage());
        }
    }

    @Override
    public void paymentAborted() {
        this.log("Payment Aborted");

        try {
            JSONObject data = new JSONObject();
            data.put("success", false);
            this.paymentSendListener(data);
        }
        catch (Exception e) {
            this.log("Erro: " + e.getMessage());
        }
    }

    @Override
    public void cardholderSignatureRequested() {
        try {
            this.log("cardholderSignatureRequested");

            if(this.listenerCardholderSignatureRequested != null) {
                this.listenerCardholderSignatureRequested.success();
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerCardholderSignatureRequested);
        }
    }

    @Override
    public void currentChargeCanBeAbortedByUser(boolean b) {
        try {
            this.log("currentChargeCanBeAbortedByUser");

            if(this.listenerCurrentChargeCanBeAbortedByUser != null) {
                this.listenerCurrentChargeCanBeAbortedByUser.success(b ? 1 : 0);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerCurrentChargeCanBeAbortedByUser);
        }
    }


    @Override
    public void signatureResult(int i) {
        try {
            this.log("signatureResult: " + i);

            if(this.listenerSignatureResult != null) {
                this.listenerSignatureResult.success(i);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerSignatureResult);
        }
    }

    @Override
    public void showMessage(String s, TerminalMessageType terminalMessageType) {
        try {
            this.log("listenerShowMessage: " + s);

            if(this.listenerShowMessage != null) {
                JSONObject data = new JSONObject();
                data.put("msg", s);
                this.listenerShowMessage.success(data);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerShowMessage);
        }
    }

    @Override
    public void showMessage(String s, TerminalMessageType terminalMessageType, String s1) {
        try {
            this.log("listenerShowMessage: " + s);

            if(this.listenerShowMessage != null) {
                JSONObject data = new JSONObject();
                data.put("msg", s);
                data.put("msg2", s1);
                this.listenerShowMessage.success(data);
            }
        }
        catch (Exception e) {
            this.error(e, this.listenerShowMessage);
        }
    }


    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if(action.equals("startDiscovery")) {
            terminalListManager = new TerminalListManager(this, this.cordova.getActivity().getApplicationContext());
            terminalListManager.startTerminalsDiscovery();
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
            final JSONObject device = args.getJSONObject(0);
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


        // Payments

        else if(action.equals("payment")) {
            this.listenerPayment = callbackContext;
            return true;
        }

        else if(action.equals("cardholderSignatureRequested")) {
            this.listenerCardholderSignatureRequested = callbackContext;
            return true;
        }

        else if(action.equals("currentChargeCanBeAbortedByUser")) {
            this.listenerCurrentChargeCanBeAbortedByUser = callbackContext;
            return true;
        }

        else if(action.equals("signatureResult")) {
            this.listenerSignatureResult = callbackContext;
            return true;
        }

        else if(action.equals("showMessage")) {
            this.listenerShowMessage = callbackContext;
            return true;
        }

        // execute order
        else if(action.equals("charge")) {

            try {

                // vars
                final double valueToCharge = args.getDouble(0);
                final int paymentOption = args.getInt(1);
                final int iNumberOfInstallments = args.getInt(2);
                final String marketplaceId = args.getString(3);
                final String sellerId = args.getString(4);
                final String publishableKey = args.getString(5);
                final String joMetadado = args.getJSONObject(6);
                final String referenceId = args.getString(7);

                Log.d("MGD", "TESTE:::::: " + joMetadado.toString());

                ZoopTerminalPayment zoopTerminalPayment = new ZoopTerminalPayment();
                zoopTerminalPayment.setTerminalPaymentListener(this);
                zoopTerminalPayment.setApplicationDisplayListener(this);
                //zoopTerminalPayment.setExtraCardInformationListener(this);

                zoopTerminalPayment.charge(new BigDecimal(valueToCharge),
                                         paymentOption,
                                         iNumberOfInstallments,
                                         marketplaceId,
                                         sellerId,
                                         publishableKey,
                                         joMetadado,
                                         referenceId);
            }
            catch (Exception e) {
                this.log("Erro: " + e.getMessage());
                return false;
            }

            return true;
        }

        else
            return false;

    }
}
