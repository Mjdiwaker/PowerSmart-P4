package com.neotechindia.plugsmart.Utilility;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.application.PlugSmartApplication;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IimagePicker;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomDialog {
    public static final String TAG = CustomDialog.class.getName();
    static CustomDialog customDialog = new CustomDialog();
    public static boolean isShowingDuRegistration = false;
    public static String du_pkt = null;
    private static Dialog dialog;
    public static ArrayAdapter<String> adapter;

    private CustomDialog() {
    }

    public static CustomDialog getInstance() {
        return customDialog;
    }

    /*
     *show alert dialog.
     */
    public static AlertDialog showAlertDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ok, null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static void showDialog(Context context, String title,
                                  String message, String btn1Text, String btn2Text,
                                  final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        ((TextView) dialog.findViewById(R.id.id_dialog_msg))
                .setText(message);
        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_cancel_txt_view);
        TextView btn2 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }
        btn1.setText(btn1Text);
        btn2.setText(btn2Text);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonTwoClick();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonOneClick();

            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }


    public static void showDialogOk(Context context, String title,
                                    String message, String btn1Text,
                                    final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_ok_alert);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        ((TextView) dialog.findViewById(R.id.id_dialog_msg))
                .setText(message);

        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }
        btn1.setText(btn1Text);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonOneClick();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

    }

    public static void showDeviceInfo(final Context context, String temp, String powerConsumption,

                                      final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_device_info);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);

        TextView tv_temp, tv_power_consumption;
        tv_temp = dialog.findViewById(R.id.tv_temp);
        tv_power_consumption = dialog.findViewById(R.id.tv_power_consumption);

        tv_temp.setText("Temp: " + temp);
        tv_power_consumption.setText("Power Consumption: " + powerConsumption);

        tv_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_power_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    public static void showScheduleDialogDaily(final Context context, String dateString,

                                               final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_daily);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView tv_daily, tv_mon_to_fri, tv_custom;
        tv_daily = dialog.findViewById(R.id.tv_daily);
        tv_mon_to_fri = dialog.findViewById(R.id.tv_mon_to_fri);
        tv_custom = dialog.findViewById(R.id.tv_custom);
        char[] dayArr = dateString.toCharArray();
        if (!dateString.equalsIgnoreCase("")) {

            switch (dateString) {
                case "12345":
                    tv_mon_to_fri.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    tv_mon_to_fri.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_right, 0, 0, 0);
                    tv_daily.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    tv_custom.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    break;
                case "1234567":
                    tv_daily.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    tv_daily.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_right, 0, 0, 0);
                    tv_mon_to_fri.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    tv_custom.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    break;
                default:
                    tv_custom.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    tv_custom.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_right, 0, 0, 0);
                    tv_mon_to_fri.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    tv_daily.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    break;
            }

        }

        tv_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onButtonOneClick();
            }
        });
        tv_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = "1234567";

                if (res.length() > 0) {
                    dialog.dismiss();
                    listener.onButtonThreeClick(res);
                } else {
                    Toast.makeText(context, "Please Select atleast one day", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_mon_to_fri.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String res = "12345";

                if (res.length() > 0) {
                    dialog.dismiss();
                    listener.onButtonThreeClick(res);
                } else {
                    Toast.makeText(context, "Please Select atleast one day", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    public static void showScheduleDialog(final Context context, String dateString,

                                          final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_days);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout ll_monday, ll_tuesday, ll_wednesday, ll_thursday, ll_friday, ll_saturday, ll_sunday;
        final CheckBox cb_monday, cb_tuesday, cb_wed, cb_thur, cb_fri, cb_sat, cb_sun;
        TextView tv_cancel, tv_ok;
        ll_tuesday = dialog.findViewById(R.id.ll_tuesday);
        ll_monday = dialog.findViewById(R.id.ll_monday);
        ll_wednesday = dialog.findViewById(R.id.ll_wednesday);
        ll_thursday = dialog.findViewById(R.id.ll_thursday);
        ll_friday = dialog.findViewById(R.id.ll_friday);
        ll_saturday = dialog.findViewById(R.id.ll_saturday);
        ll_sunday = dialog.findViewById(R.id.ll_sunday);
        cb_monday = dialog.findViewById(R.id.cb_monday);
        cb_tuesday = dialog.findViewById(R.id.cb_tuesday);
        cb_wed = dialog.findViewById(R.id.cb_wed);
        cb_thur = dialog.findViewById(R.id.cb_thur);
        cb_fri = dialog.findViewById(R.id.cb_fri);
        cb_sat = dialog.findViewById(R.id.cb_sat);
        cb_sun = dialog.findViewById(R.id.cb_sun);
        tv_cancel = dialog.findViewById(R.id.tv_cancel);
        tv_ok = dialog.findViewById(R.id.tv_ok);
        char[] dayArr = dateString.toCharArray();
        if (!dateString.equalsIgnoreCase("")) {
            for (char c : dayArr) {
                switch (c) {
                    case '1':
                        cb_monday.setChecked(true);
                        break;
                    case '2':
                        cb_tuesday.setChecked(true);
                        break;
                    case '3':
                        cb_wed.setChecked(true);
                        break;
                    case '4':
                        cb_thur.setChecked(true);
                        break;
                    case '5':
                        cb_fri.setChecked(true);
                        break;
                    case '6':
                        cb_sat.setChecked(true);
                        break;
                    case '7':
                        cb_sun.setChecked(true);
                        break;
                }
            }
        }
        ll_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_monday.setChecked(true);
            }
        });
        ll_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_tuesday.setChecked(true);
            }
        });
        ll_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_wed.setChecked(true);
            }
        });
        ll_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_thur.setChecked(true);
            }
        });
        ll_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_fri.setChecked(true);
            }
        });
        ll_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_sat.setChecked(true);
            }
        });
        ll_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_sun.setChecked(true);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonTwoClick();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String res = "";
                if (cb_monday.isChecked()) {
                    res += "1";
                }
                if (cb_tuesday.isChecked()) {
                    res += "2";
                }
                if (cb_wed.isChecked()) {
                    res += "3";
                }
                if (cb_thur.isChecked()) {
                    res += "4";
                }
                if (cb_fri.isChecked()) {
                    res += "5";
                }
                if (cb_sat.isChecked()) {
                    res += "6";
                }
                if (cb_sun.isChecked()) {
                    res += "7";
                }
                if (res.length() > 0) {
                    dialog.dismiss();
                    listener.onButtonThreeClick(res);
                } else {
                    Toast.makeText(context, "Please Select atleast one day", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    public static void showYesNoAlert(Context context, String title,
                                      String message, String btn1Text, String btn2Text,
                                      final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_image_dialog);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        ((TextView) dialog.findViewById(R.id.id_dialog_msg))
                .setText(message);
        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        TextView btn2 = (TextView) dialog
                .findViewById(R.id.id_cancel_txt_view);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }
        btn1.setText(btn1Text);
        btn2.setText(btn2Text);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonOneClick();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonTwoClick();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

    }

    public static void showDialogEditText(final Context context, String title,
                                          String message, String btn1Text, String btn2Text,
                                          final AquasmartDialogListener listener, final DeviceBean deviceBean) {
        dialog = new Dialog(context);
        final LinearLayout dialogLayout;
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_edit_dialog);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        dialogLayout = (LinearLayout) dialog.findViewById(R.id.dialogLayout);
        final EditText editText = ((EditText) dialog.findViewById(R.id.id_dialog_msg));
        editText.setHint(message);
        final ImageView imageView = ((ImageView) dialog.findViewById(R.id.iv_password_visible));
        imageView.setEnabled(false);
        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_cancel_txt_view);
        TextView btn2 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }
        btn1.setText(btn1Text);
        btn2.setText(btn2Text);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() <= 0) {
                    CustomDialog.getInstance().showSnackBar(dialogLayout, "Enter Admin Password");
                    //editText.setError("Enter Admin Password");
                    return;
                }
                if (editText.getText().toString().length() >= 6 && editText.getText().toString().length() <= 12) {
                    if (ConstantUtil.isForAPModeOnly) {
                        MyWifiReceiver.write(PacketsUrl.verifyingAdminPassword(editText.getText().toString()), CommandUtil.cmd_password_veryfication);
                    } else {
                        HashMap<String, Socket> hashMap = new HashMap<>();
                        hashMap = PlugSmartApplication.getHashMap();
                        Iterator it = hashMap.entrySet().iterator();
                        Socket mac = null;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            if (deviceBean.macId.equalsIgnoreCase(pair.getKey().toString())) {
                                mac = (Socket) pair.getValue();
                                break;
                            }
                            System.out.println(pair.getKey() + " = " + pair.getValue());
                            // it.remove(); // avoids a ConcurrentModificationException
                        }
                        if (mac != null) {
                            SocketUtil.write(PacketsUrl.verifyingAdminPassword(editText.getText().toString()), deviceBean.ip, mac, deviceBean.macId, 0, context);
                        }
                    }


                    dialog.dismiss();
                    listener.onButtonOneClick();

                    return;
                } else {
                    CustomDialog.getInstance().showSnackBar(dialogLayout, "Invalid Password");
                    //editText.setError("Invalid Password");
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //imageView.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                imageView.setEnabled(s.length() > 0 ? true : false);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                final boolean isOutsideView = event.getX() < 0 ||
                        event.getX() > v.getWidth() ||
                        event.getY() < 0 ||
                        event.getY() > v.getHeight();
                final int cursor = editText.getSelectionStart();

                if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                editText.setSelection(cursor);
                return true;
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message1 = ((EditText) dialog.findViewById(R.id.id_dialog_msg)).getText().toString();
                dialog.dismiss();
                listener.onButtonThreeClick(message1);
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


    }

    public static void showDialogEditTextOneButton(Context context, String title,
                                                   String message, String btn1Text,
                                                   final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_edit_dialog);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        final EditText editText = ((EditText) dialog.findViewById(R.id.id_dialog_msg));
        editText.setHint(message);
        final ImageView imageView = ((ImageView) dialog.findViewById(R.id.iv_password_visible));
        imageView.setEnabled(false);
        TextView btn2 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_cancel_txt_view);
        btn1.setVisibility(View.INVISIBLE);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }
        btn2.setText(btn1Text);
        // btn2.setText(btn2Text);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() >= 6 && editText.getText().toString().length() <= 12) {
                    dialog.dismiss();
                    listener.onButtonOneClick();
                    MyWifiReceiver.write(PacketsUrl.verifyingAdminPassword(editText.getText().toString()), CommandUtil.cmd_password_veryfication);
                    return;
                }
                editText.setError("Password length must be greater than 5 and less than 13");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //imageView.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                imageView.setEnabled(s.length() > 0 ? true : false);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                final boolean isOutsideView = event.getX() < 0 ||
                        event.getX() > v.getWidth() ||
                        event.getY() < 0 ||
                        event.getY() > v.getHeight();
                final int cursor = editText.getSelectionStart();

                if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                editText.setSelection(cursor);
                return true;
            }
        });


//        btn1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String message1 = ((EditText) dialog.findViewById(R.id.id_dialog_msg)).getText().toString();
//                dialog.dismiss();
//                listener.onButtonThreeClick(message1);
//            }
//        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);

    }

    public static void showDialogConfigureSettings(final Context context, String title,
                                                   String message, String btn1Text, String btn2Text,
                                                   final AquasmartDialogListener listener, final View baseLayout) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_edit_dialog);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        final EditText editText = ((EditText) dialog.findViewById(R.id.id_dialog_msg));
        editText.setHint(message);
        final ImageView imageView = ((ImageView) dialog.findViewById(R.id.iv_password_visible));
        imageView.setEnabled(false);
        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_cancel_txt_view);
        TextView btn2 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }

        btn1.setText(btn1Text);
        btn2.setText(btn2Text);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() <= 0) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "Enter Admin Password");
                    // editText.setError("Enter Admin Password");
                    return;
                }
                if (editText.getText().toString().length() >= 6 && editText.getText().toString().length() <= 12) {
                    dialog.dismiss();
                    listener.onButtonOneClick();
                    MyWifiReceiver.write(PacketsUrl.verifyingAdminPassword(editText.getText().toString()), CommandUtil.cmd_password_veryfication);
                    //  AquaSmartPreference.getInstance(context).setConfigCancelled(true);
                    return;
                } else {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "Invalid Password");
                    //editText.setError("Invalid Password");
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //imageView.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                imageView.setEnabled(s.length() > 0 ? true : false);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                final boolean isOutsideView = event.getX() < 0 ||
                        event.getX() > v.getWidth() ||
                        event.getY() < 0 ||
                        event.getY() > v.getHeight();
                final int cursor = editText.getSelectionStart();

                if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                editText.setSelection(cursor);
                return true;
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message1 = ((EditText) dialog.findViewById(R.id.id_dialog_msg)).getText().toString();
                dialog.dismiss();
                //USBBroadcastReceiver.write(PacketsUrl.getWaterlevelConsolidated());
                // USBBroadcastReceiver.write(PacketsUrl.getWaterlevel());
                // showProgressDialog(context);
                // AquaSmartPreference.getInstance(context).setConfigCancelled(true);
                listener.onButtonThreeClick(message1);
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


    }

    /**
     * @param parentView : RootView Id
     * @param msg        : message will display on snackBar
     * @para/**
     * @author Sumit Agrawal
     */
    public void showSnackBar(View parentView, String msg) {
        Snackbar.make(parentView, msg, Snackbar.LENGTH_SHORT).show();
    }

    public static void showDialogInfoOk(Context context, String title,
                                        String message, String btn1Text,
                                        final AquasmartDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_ok_alert);
        ((TextView) dialog.findViewById(R.id.id_dialog_title))
                .setText(title);
        ((TextView) dialog.findViewById(R.id.id_dialog_msg))
                .setText(message);
        ((TextView) dialog.findViewById(R.id.id_dialog_msg)).setGravity(Gravity.LEFT);
        ((TextView) dialog.findViewById(R.id.id_dialog_msg)).setPadding(5, 5, 5, 5);
        TextView btn1 = (TextView) dialog
                .findViewById(R.id.id_delete_txt_view);
        if (title.equals("")) {
            dialog.findViewById(R.id.id_dialog_title).setVisibility(View.GONE);
        }
        btn1.setText(btn1Text);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onButtonOneClick();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

    }

    public static void showDialogTimer(Context context,
                                       final AquasmartDialogListener listener) {
        int hour, min, sec, time;
        final NumberPicker numberPickerHour, numberPickerMin, numberPickerSec;
        Button btn_cancel, btn_save;
        boolean isTimerSet = true;
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_timer);
        numberPickerHour = dialog.findViewById(R.id.numberPickerHour);
        numberPickerMin = dialog.findViewById(R.id.numberPickerMin);
        numberPickerSec = dialog.findViewById(R.id.numberPickerSec);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_save = dialog.findViewById(R.id.btn_save);

        numberPickerHour.setMinValue(15);
        numberPickerHour.setMaxValue(120);
        numberPickerHour.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        numberPickerMin.setMinValue(15);
        numberPickerMin.setMaxValue(120);
        numberPickerMin.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        numberPickerSec.setMinValue(15);
        numberPickerSec.setMaxValue(120);
        numberPickerSec.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onButtonTwoClick();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = ((numberPickerHour.getValue() * 60) + numberPickerMin.getValue()) * 60 + numberPickerSec.getValue();
                dialog.dismiss();
                listener.onButtonThreeClick(String.valueOf(time));


            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

    }

    public void dismissDialog(Context context) {
        if (dialog != null) {
            dialog.dismiss();

        }
    }
    public static void showImageDialog(final Context context,ArrayList<Integer> arrayList,final IimagePicker listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_picker);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        final MediaPlayer player = MediaPlayer.create(context, R.raw.tirando);
        player.setLooping(false); // Set looping
        player.setVolume(100,100);
        final int mPlayerLength = 0;
        Animation animation;
        animation= AnimationUtils.loadAnimation(context.getApplicationContext(),
                R.anim.bounce);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(player.isPlaying()){
                    player.stop();
                    player.release();

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        lp.copyFrom(dialog.getWindow().getAttributes());
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.80);
        lp.width = width;
        lp.height = height;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
        RecyclerView rv_image_picker;
        RecyclerView.LayoutManager layoutManager;
        rv_image_picker = dialog.findViewById(R.id.rv_image_picker);
        layoutManager = new GridLayoutManager(context,2);
        rv_image_picker.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_image_picker.getContext(),
                DividerItemDecoration.HORIZONTAL);
        rv_image_picker.addItemDecoration(dividerItemDecoration);
/*
        ImagePickerAdapter adapter=new ImagePickerAdapter(context,arrayList, new IimagePicker() {
            @Override
            public void image(Integer i) {
                dialog.dismiss();
                listener.image(i);
            }


        });*/
     /*   rv_image_picker.setAdapter(adapter);*/




        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

}
