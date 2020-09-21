package com.neotechindia.plugsmart.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.adapter.SchedulesListAdapter;
import com.neotechindia.plugsmart.adapter.SchedulesListNewAdapter;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IPacketReceiveCallBack;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.listeners.RecyclerItemClickListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.JsonDataFieldsModel;
import com.neotechindia.plugsmart.model.LicenceType;
import com.neotechindia.plugsmart.model.RecyclerSelectorScheduleModel;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;
import com.neotechindia.plugsmart.requestor.SocketRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


public class SchedulesViewActivity extends AppCompatActivity implements View.OnClickListener, IUpdateUiListener, RecyclerItemClickListener, SchedulesListNewAdapter.OnListItemClickCallBack, IPacketReceiveCallBack {
    private Toolbar toolbar;
    private RecyclerView rv_list_schedule;
    private TextView tv_empty;
    private DeviceBean deviceBean;
    private ImageView iv_delete, iv_edit, iv_create;
    private JsonDataFieldsModel jsonDataFieldsModel;
    private ArrayList<JsonDataFieldsModel> arrayList;
    private SchedulesListAdapter schedulesListAdapter;
    private SchedulesListNewAdapter.OnListItemClickCallBack mOnListItemClickCallBack;
    private CountDownTimer countDownTimer;
    private boolean isPacketReceived = false;
    boolean isTimerSet = true;
    private int sizeforcreatebutton = 0;
    private SchedulesListNewAdapter mSchedulesListNewAdapter;
    private int position = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules_view);
        ConstantUtil.updateListener = this;
        ConstantUtil.recyclerItemClickListener = this;
        this.mOnListItemClickCallBack = this;
        MyWifiReceiver.setOnPacketReceiveCallBack(this);
        SocketRequester.setOnPacketReceiveCallBack(this);
        initViews();
        setListeners();
        getSchedules();
    }

    private void getSchedules() {
        if (ConstantUtil.isForAPModeOnly) {
            MyWifiReceiver.write(PacketsUrl.getSchedulerList(), CommandUtil.cmd_get_schedulers);
        } else {
            Util.sendPacketStationMode(deviceBean, PacketsUrl.getSchedulerList(), SchedulesViewActivity.this);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isPacketReceived = false;
        timerResponseDialog();
    }

    private void timerResponseDialog() {
        Util.showLoader(SchedulesViewActivity.this);
        countDownTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Util.hideLoader();
                        if (!isPacketReceived) {
                            Toast.makeText(SchedulesViewActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                        }

                    }
                });
                countDownTimer = null;
            }


        }.start();
    }

    private void setListeners() {
        iv_edit.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        iv_create.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_list_schedule.setLayoutManager(layoutManager);
    }

    private Paint p = new Paint();

    private void initViews() {
        arrayList = new ArrayList<>();
        jsonDataFieldsModel = new JsonDataFieldsModel();
        deviceBean = new DeviceBean();
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_schedules));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        rv_list_schedule = findViewById(R.id.rv_list_schedule);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_list_schedule.getContext(),
                DividerItemDecoration.VERTICAL);
        rv_list_schedule.addItemDecoration(dividerItemDecoration);
        tv_empty = findViewById(R.id.tv_empty);
        iv_create = findViewById(R.id.iv_create);
        iv_delete = findViewById(R.id.iv_delete);
        iv_edit = findViewById(R.id.iv_edit);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                /*       if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){*/
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    Log.i("dx", String.valueOf(dX));
                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.editfaded);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.deletefaded);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(SchedulesViewActivity.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message
                    builder.setCancelable(false);
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JsonDataFieldsModel jsonDataFieldsModel = arrayList.get(position);
                            if (ConstantUtil.isForAPModeOnly) {
                                MyWifiReceiver.write(PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "3"), CommandUtil.cmd_create_or_save_schedule);
                            } else {
                                Util.sendPacketStationMode(deviceBean, PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "3"), SchedulesViewActivity.this);
                            }

                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_empty.setVisibility(View.GONE);
                            rv_list_schedule.setVisibility(View.VISIBLE);
                            schedulesListAdapter = new SchedulesListAdapter(SchedulesViewActivity.this, arrayList, deviceBean, -1);

                            rv_list_schedule.setAdapter(schedulesListAdapter);
                            return;
                        }
                    }).show();  //show alert dialog
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SchedulesViewActivity.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to edit?");    //set message
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JsonDataFieldsModel jsonDataFieldsModel = arrayList.get(position);
                           /* RecyclerSelectorScheduleModel recyclerSelectorScheduleModel = new RecyclerSelectorScheduleModel();
                            recyclerSelectorScheduleModel.one = jsonDataFieldsModel.getOne();
                            recyclerSelectorScheduleModel.two = jsonDataFieldsModel.getTwo();
                            recyclerSelectorScheduleModel.three = jsonDataFieldsModel.getThree();
                            recyclerSelectorScheduleModel.four = jsonDataFieldsModel.getFour();
                            recyclerSelectorScheduleModel.five = jsonDataFieldsModel.getFive();*/
                            Intent i = new Intent(SchedulesViewActivity.this, SchedulesCreateActivity.class);
                            i.putExtra(ConstantUtil.editSchedule, position);
                            i.putExtra(ConstantUtil.scheduleList, arrayList);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.putExtra(ConstantUtil.ID, "1");
                            startActivity(i);
                            finish();
                            //   recyclerSelectorScheduleModel = null;
                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_empty.setVisibility(View.GONE);
                            rv_list_schedule.setVisibility(View.VISIBLE);
                            schedulesListAdapter = new SchedulesListAdapter(SchedulesViewActivity.this, arrayList, deviceBean, -1);

                            rv_list_schedule.setAdapter(schedulesListAdapter);
                            return;
                        }
                    }).show();  //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_list_schedule); //set swipe to recylcerview
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_create:
                if (sizeforcreatebutton == LicenceType.GetScheduleNumber(deviceBean.LicenceType)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SchedulesViewActivity.this, "Schedule Limit Reached", Toast.LENGTH_SHORT).show();
                        }
                    });


                    return;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SchedulesViewActivity.this, SchedulesCreateActivity.class);
                            i.putExtra(ConstantUtil.scheduleList, arrayList);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.putExtra(ConstantUtil.ID, "2");
                            startActivity(i);
                            finish();
                        }
                    });
                }
                break;

            case R.id.iv_delete:
                //  if (recyclerSelectorScheduleModel == null) {
                if (position == -1) {
                    Toast.makeText(SchedulesViewActivity.this, "Please select schedule for delete", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    deleteDialog();
                }

                break;
            case R.id.iv_edit:
                if (sizeforcreatebutton == 0) {
                    return;
                }
                int startMinutes = 0, endMinutes = 0;
                String days = null, scheduleId = null, scheduleEnable = null;
//                if (recyclerSelectorScheduleModel != null) {
                if (position != -1) {
                    JsonDataFieldsModel jsonDataFieldsModel = arrayList.get(position);
                    startMinutes = Integer.parseInt(jsonDataFieldsModel.getThree());
                    endMinutes = Integer.parseInt(jsonDataFieldsModel.getFour());
                    days = jsonDataFieldsModel.getFive();
                    scheduleId = jsonDataFieldsModel.getOne();
                    scheduleEnable = jsonDataFieldsModel.getTwo();
                }
                // if (recyclerSelectorScheduleModel == null) {
                if (position == -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SchedulesViewActivity.this, "Please Select Schedule for edit", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                } else if (scheduleEnable.equalsIgnoreCase("2")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SchedulesViewActivity.this, "Please enable Schedule for edit", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                } else {
                    Calendar cal = Calendar.getInstance();
                    String date = "" + cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR) + "day: " + cal.get(Calendar.DAY_OF_WEEK);
                    String day = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
                    if (Integer.parseInt(day) > 1) {
                        day = String.valueOf(Integer.parseInt(day) - 1);
                    } else {
                        day = String.valueOf(Integer.parseInt(day) + 6);
                    }
                    int checkTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
                    if (days.contains(day) && startMinutes <= checkTime && endMinutes >= checkTime) {
                        CustomDialog.showDialog(this, getResources().getString(R.string.edit_running_schedule),
                                getResources().getString(R.string.do_you_want_to_edit), "Cancel", "Yes", new AquasmartDialogListener() {
                                    @Override
                                    public void onButtonOneClick() {
                                        try {
                                            Intent i = new Intent(SchedulesViewActivity.this, SchedulesCreateActivity.class);
                                            i.putExtra(ConstantUtil.editSchedule, position);
                                            i.putExtra(ConstantUtil.scheduleList, arrayList);
                                            i.putExtra(ConstantUtil.stationList, deviceBean);
                                            i.putExtra(ConstantUtil.ID, "1");
                                            startActivity(i);
                                            finish();
                                            //recyclerSelectorScheduleModel = null;

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(SchedulesViewActivity.this, "Some internal error occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onButtonTwoClick() {

                                    }

                                    @Override
                                    public void onButtonThreeClick(String data) {

                                    }
                                });

                    } else {
                        try {
                            CustomDialog.showDialog(this, "Alert!!!",
                                    getResources().getString(R.string.do_you_want_to_edit), "Cancel", "Yes", new AquasmartDialogListener() {
                                        @Override
                                        public void onButtonOneClick() {
                                            try {
                                                Intent i = new Intent(SchedulesViewActivity.this, SchedulesCreateActivity.class);
                                                i.putExtra(ConstantUtil.editSchedule, position);
                                                i.putExtra(ConstantUtil.scheduleList, arrayList);
                                                i.putExtra(ConstantUtil.stationList, deviceBean);
                                                i.putExtra(ConstantUtil.ID, "1");
                                                startActivity(i);
                                                finish();
                                                //      recyclerSelectorScheduleModel = null;

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(SchedulesViewActivity.this, "Some internal error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onButtonTwoClick() {

                                        }

                                        @Override
                                        public void onButtonThreeClick(String data) {

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SchedulesViewActivity.this, "Some internal error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
     /*    "{\"sid\":\"9000\",\"sad\":\"x\",\"cmd\":\"7009\",
        \"data\":[{\"01\":\"1\",\"02\":\"1\",\"03\":\"1024\",\"04\":\"1026\",
        \"05\":\"1234567\"},{\"01\":\"2\",\"02\":\"1\",\"03\":\"1082\",\"04\":\"1085\",
        \"05\":\"1234567\"},{\"01\":\"3\",\"02\":\"1\",\"03\":\"1027\",\"04\":\"1029\",
        \"05\":\"1234567\"},{\"01\":\"4\",\"02\":\"1\",\"03\":\"1034\",\"04\":\"1036\",\"05\":\"1234567\"}]}"*/
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);

        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_schedulers)) {
            isPacketReceived = true;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            arrayList = new ArrayList<>();
            JSONArray array = null;
            try {
                array = new JSONArray(data);
                if (array.length() != 0) {
                    tv_empty.setVisibility(View.GONE);
                    arrayList.clear();
                    for (int n = 0; n < array.length(); n++) {
                        jsonDataFieldsModel = new JsonDataFieldsModel();
                        JSONObject object = array.getJSONObject(n);
                        jsonDataFieldsModel.setOne(JsonUtil.getJsonDataByField("01", object.toString()));
                        jsonDataFieldsModel.setTwo(JsonUtil.getJsonDataByField("02", object.toString()));
                        jsonDataFieldsModel.setThree(JsonUtil.getJsonDataByField("03", object.toString()));
                        jsonDataFieldsModel.setFour(JsonUtil.getJsonDataByField("04", object.toString()));
                        jsonDataFieldsModel.setFive(JsonUtil.getJsonDataByField("05", object.toString()));
                        // do some stuff....

                        arrayList.add(jsonDataFieldsModel);
                    }
                    sizeforcreatebutton = arrayList.size();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_empty.setVisibility(View.GONE);
                            rv_list_schedule.setVisibility(View.VISIBLE);
                            mSchedulesListNewAdapter = new SchedulesListNewAdapter(SchedulesViewActivity.this, arrayList, mOnListItemClickCallBack);
                            rv_list_schedule.setAdapter(mSchedulesListNewAdapter);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rv_list_schedule.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setText("No Data");
                        }
                    });
                    sizeforcreatebutton = 0;
                }
                if (data.equalsIgnoreCase("18")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setButtonsVisibility(0);
                            rv_list_schedule.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setText("No Data");
                        }
                    });
                    sizeforcreatebutton = 0;
                }
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
                } else {
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), SchedulesViewActivity.this);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } /*else if (cmd.equalsIgnoreCase(CommandUtil.cmd_create_or_save_schedule)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            String scheduleId = JsonUtil.getJsonDataByField("01", data);
            String status = JsonUtil.getJsonDataByField("02", data);
            if (status.equalsIgnoreCase("1")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Enabled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (status.equalsIgnoreCase("2")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Disabled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (status.equalsIgnoreCase("3")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (ConstantUtil.isForAPModeOnly) {
                MyWifiReceiver.write(PacketsUrl.getSchedulerList(), CommandUtil.cmd_get_schedulers);
                MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
            } else {
                Util.sendPacketStationMode(deviceBean, PacketsUrl.getSchedulerList(), SchedulesViewActivity.this);
                Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), SchedulesViewActivity.this);
            }
        } */ else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_timer)) {
            if (data.equalsIgnoreCase("00000")) {
                isTimerSet = false;
            } else {
                isTimerSet = true;
            }
        }
    }

    private void setScheduleActionBar(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isEnable) {
                    iv_delete.setImageResource(R.drawable.deletefaded);
                    iv_edit.setImageResource(R.drawable.editfaded);
                } else {
                    iv_delete.setImageResource(R.drawable.delete_grey);
                    iv_edit.setImageResource(R.drawable.edit_grey);
                }
            }
        });
    }

    private void setButtonsVisibility(int size) {
        sizeforcreatebutton = size;
        if (size == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_delete.setImageResource(R.drawable.delete_grey);
                    iv_edit.setImageResource(R.drawable.edit_grey);
                }
            });
        }
        if (size == 2 || size == 1 || size == 3 || size == 4) {
            setScheduleActionBar(true);
        }
        if (size == 5) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_create.setEnabled(true);
                    iv_delete.setImageResource(R.drawable.deletefaded);
                    iv_edit.setImageResource(R.drawable.editfaded);
                }
            });
        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);

        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_schedulers)) {
            isPacketReceived = true;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            arrayList = new ArrayList<>();
            JSONArray array = null;
            try {
                array = new JSONArray(data);
                if (array.length() != 0) {
                    tv_empty.setVisibility(View.GONE);
                    for (int n = 0; n < array.length(); n++) {
                        jsonDataFieldsModel = new JsonDataFieldsModel();
                        JSONObject object = array.getJSONObject(n);
                        jsonDataFieldsModel.setOne(JsonUtil.getJsonDataByField("one", object.toString()));
                        jsonDataFieldsModel.setTwo(JsonUtil.getJsonDataByField("two", object.toString()));
                        jsonDataFieldsModel.setThree(JsonUtil.getJsonDataByField("three", object.toString()));
                        jsonDataFieldsModel.setFour(JsonUtil.getJsonDataByField("four", object.toString()));
                        jsonDataFieldsModel.setFive(JsonUtil.getJsonDataByField("five", object.toString()));
                        // do some stuff....
                        arrayList.add(jsonDataFieldsModel);
                    }
                    sizeforcreatebutton = arrayList.size();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_empty.setVisibility(View.GONE);
                            rv_list_schedule.setVisibility(View.VISIBLE);
                            mSchedulesListNewAdapter = new SchedulesListNewAdapter(SchedulesViewActivity.this, arrayList, mOnListItemClickCallBack);
                            rv_list_schedule.setAdapter(mSchedulesListNewAdapter);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rv_list_schedule.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setText("No Data");
                        }
                    });
                    sizeforcreatebutton = 0;
                }
                if (data.equalsIgnoreCase("18")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setButtonsVisibility(0);
                            rv_list_schedule.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setText("No Data");
                        }
                    });
                    sizeforcreatebutton = 0;
                }
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
                } else {
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), SchedulesViewActivity.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }/* else if (cmd.equalsIgnoreCase(CommandUtil.cmd_create_or_save_schedule)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            String scheduleId = JsonUtil.getJsonDataByField("01", data);
            String status = JsonUtil.getJsonDataByField("02", data);
            if (status.equalsIgnoreCase("1")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Enabled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (status.equalsIgnoreCase("2")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Disabled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (status.equalsIgnoreCase("3")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
         *//*   if (ConstantUtil.isForAPModeOnly) {
                MyWifiReceiver.write(PacketsUrl.getSchedulerList(), CommandUtil.cmd_get_schedulers);
                MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
            } else {
                Util.sendPacketStationMode(deviceBean, PacketsUrl.getSchedulerList(), SchedulesViewActivity.this);
                Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), SchedulesViewActivity.this);

            }*//*
        }*/ else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_timer)) {
            if (data.equalsIgnoreCase("00000")) {
                isTimerSet = false;
            } else {
                isTimerSet = true;
            }
        }
    }

    private void deleteDialog() {
        Calendar cal = Calendar.getInstance();
        String date = "" + cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR) + "day: " + cal.get(Calendar.DAY_OF_WEEK);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        if (Integer.parseInt(day) > 1) {
            day = String.valueOf(Integer.parseInt(day) - 1);
        } else {
            day = String.valueOf(Integer.parseInt(day) + 6);
        }
        final JsonDataFieldsModel jsonDataFieldsModel = arrayList.get(position);
        int checkTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        int startMinutes = Integer.parseInt(jsonDataFieldsModel.getThree());
        int endMinutes = Integer.parseInt(jsonDataFieldsModel.getFour());
        if (jsonDataFieldsModel.getFive().contains(day) && startMinutes <= checkTime && endMinutes >= checkTime) {
            CustomDialog.showDialog(this, getResources().getString(R.string.delete_running_schedule),
                    getResources().getString(R.string.do_you_want_to_delete), "Cancel", "Yes", new AquasmartDialogListener() {
                        @Override
                        public void onButtonOneClick() {
                            if (ConstantUtil.isForAPModeOnly) {
                                MyWifiReceiver.write(PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "3"), CommandUtil.cmd_create_or_save_schedule);
                            } else {
                                Util.sendPacketStationMode(deviceBean, PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "3"), SchedulesViewActivity.this);
                            }
                        }

                        @Override
                        public void onButtonTwoClick() {

                        }

                        @Override
                        public void onButtonThreeClick(String data) {

                        }
                    });

        } else {
            CustomDialog.showDialog(this, getResources().getString(R.string.delete_schedule),
                    getResources().getString(R.string.do_you_want_to_delete), "Cancel", "Yes", new AquasmartDialogListener() {
                        @Override
                        public void onButtonOneClick() {
                            if (ConstantUtil.isForAPModeOnly) {
                                MyWifiReceiver.write(PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "3"), CommandUtil.cmd_create_or_save_schedule);
                            } else {
                                Util.sendPacketStationMode(deviceBean, PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "3"), SchedulesViewActivity.this);
                            }
                        }

                        @Override
                        public void onButtonTwoClick() {

                        }

                        @Override
                        public void onButtonThreeClick(String data) {

                        }
                    });
        }
    }

    private RecyclerSelectorScheduleModel recyclerSelectorScheduleModel = null;

    @Override
    public void onItemClickListener(RecyclerSelectorScheduleModel recyclerSelectorScheduleModel, int pos) {
        this.recyclerSelectorScheduleModel = recyclerSelectorScheduleModel;
        schedulesListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.hideLoader();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.hideLoader();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void timerDisabledPopUp(String msg, final boolean state) {
        String msg2 = getString(R.string.timerDisabledScheduleEnabled);

        CustomDialog.getInstance().showYesNoAlert(SchedulesViewActivity.this, msg, msg2, "Yes", "No", new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {
                if (state) {
                    if (ConstantUtil.isForAPModeOnly) {
                        MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", jsonDataFieldsModel.getThree(), jsonDataFieldsModel.getFour(), jsonDataFieldsModel.getFive()), CommandUtil.cmd_create_or_save_schedule);
                    } else {
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", jsonDataFieldsModel.getThree(), jsonDataFieldsModel.getFour(), jsonDataFieldsModel.getFive()), SchedulesViewActivity.this);
                    }
                } else {

                    if (ConstantUtil.isForAPModeOnly) {
                        MyWifiReceiver.write(PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "2"), CommandUtil.cmd_create_or_save_schedule);
                    } else {
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "2"), SchedulesViewActivity.this);
                    }
                }
            }

            @Override
            public void onButtonTwoClick() {
                finish();
            }

            @Override
            public void onButtonThreeClick(String password) {

            }
        });
    }

    public void scheduleState(boolean state, JsonDataFieldsModel jsonDataFieldsModel, int position) {

        if (Logger.isPlugSmart) {
            if (isTimerSet) {
                timerDisabledPopUp("Alert!!!", state);
            } else {
                if (state) {
                    if (ConstantUtil.isForAPModeOnly) {
                        MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", jsonDataFieldsModel.getThree(), jsonDataFieldsModel.getFour(), jsonDataFieldsModel.getFive()), CommandUtil.cmd_create_or_save_schedule, position);
                    } else {
                        SocketRequester.setPosition(position);
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", jsonDataFieldsModel.getThree(), jsonDataFieldsModel.getFour(), jsonDataFieldsModel.getFive()), this);
                    }
                } else {
                    if (ConstantUtil.isForAPModeOnly) {
                        MyWifiReceiver.write(PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "2"), CommandUtil.cmd_create_or_save_schedule, position);
                    } else {
                        SocketRequester.setPosition(position);
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "2"), this);
                    }
                }
            }
        } else {
            if (state) {
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", jsonDataFieldsModel.getThree(), jsonDataFieldsModel.getFour(), jsonDataFieldsModel.getFive()), CommandUtil.cmd_create_or_save_schedule, position);
                } else {
                    SocketRequester.setPosition(position);
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", jsonDataFieldsModel.getThree(), jsonDataFieldsModel.getFour(), jsonDataFieldsModel.getFive()), this);
                }
            } else {
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "2"), CommandUtil.cmd_create_or_save_schedule, position);
                } else {
                    SocketRequester.setPosition(position);
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.deleteOrDisableSchedule(jsonDataFieldsModel.getOne(), "2"), this);
                }
            }
        }

    }

    @Override
    public void onItemClick(View view, int pos) {
        JsonDataFieldsModel model = arrayList.get(pos);

        switch (view.getId()) {
            case R.id.col_2:
                if (model.isSelected() == 1) {
                    for (JsonDataFieldsModel model1 :
                            arrayList) {
                        model1.setSelected(0);

                    }
                    model.setSelected(0);
                    this.position = -1;
                } else {
                    for (JsonDataFieldsModel model1 :
                            arrayList) {
                        model1.setSelected(0);

                    }
                    model.setSelected(1);
                    this.position = pos;
                }


                mSchedulesListNewAdapter.notifyDataSetChanged();
                break;
            case R.id.rl:
                if (model.isSelected() == 1) {
                    for (JsonDataFieldsModel model1 :
                            arrayList) {
                        model1.setSelected(0);

                    }
                    model.setSelected(0);
                    this.position = -1;
                } else {
                    for (JsonDataFieldsModel model1 :
                            arrayList) {
                        model1.setSelected(0);

                    }
                    model.setSelected(1);
                    this.position = pos;
                }


                mSchedulesListNewAdapter.notifyDataSetChanged();
                break;
            case R.id.sb_schedule:
                if (model.getTwo().equalsIgnoreCase("1")) {
                    scheduleState(false, model, pos);
                } else {
                    scheduleState(true, model, pos);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPacketReceiveSuccess(String response, final int modifiedPosition) {
        String cmd = JsonUtil.getJsonDataByField("cmd", response);
        final String data = JsonUtil.getJsonDataByField("data", response);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_create_or_save_schedule)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            String scheduleId = JsonUtil.getJsonDataByField("01", data);
            String status = JsonUtil.getJsonDataByField("02", data);
            if (status.equalsIgnoreCase("1")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arrayList.get(modifiedPosition).setTwo("1");
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Enabled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (status.equalsIgnoreCase("2")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Disabled", Toast.LENGTH_SHORT).show();
                        arrayList.get(modifiedPosition).setTwo("2");
                    }
                });
            }
            if (status.equalsIgnoreCase("3")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchedulesViewActivity.this, "Schedule is Deleted", Toast.LENGTH_SHORT).show();
                        if (ConstantUtil.isForAPModeOnly) {
                            MyWifiReceiver.write(PacketsUrl.getSchedulerList(), CommandUtil.cmd_get_schedulers);
                            MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
                        } else {
                            Util.sendPacketStationMode(deviceBean, PacketsUrl.getSchedulerList(), SchedulesViewActivity.this);
                            Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), SchedulesViewActivity.this);
                        }
                    }
                });

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSchedulesListNewAdapter.notifyDataSetChanged();
                    //  ArrayList<JsonDataFieldsModel> updatedList = mSchedulesListNewAdapter.getUpdatedList();
                    // Toast.makeText(SchedulesViewActivity.this, updatedList.size() + "", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onPacketReceiveError(String errorMessage, int modifiedPosition) {

    }
}
