package xuyihao.rongyiclient.activities.accounts;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import xuyihao.JohnsonHttpConnector.connectors.http.RequestSender;
import xuyihao.rongyiclient.activities.MainActivity;
import xuyihao.rongyiclient.R;
import xuyihao.rongyiclient.entity.Accounts;

public class LoginActivity extends AppCompatActivity {

    private RequestSender sender = MainActivity.sender;
    private Accounts accounts = MainActivity.accounts;
    private SQLiteDatabase database = MainActivity.database;

    /**
     * 控件
     */
    private EditText editTextAcc_name;
    private EditText editTextAcc_pwd;
    private Button btnLogin;
    private TextView textViewProgress;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.editTextAcc_name = (EditText)findViewById(R.id.loginActivity_editText_Acc_name);
        this.editTextAcc_pwd = (EditText)findViewById(R.id.loginActivity_editText_Acc_pwd);
        this.btnLogin = (Button)findViewById(R.id.loginActivity_button_login);
        this.textViewProgress = (TextView)findViewById(R.id.loginActivity_textView_progress);
        this.btnRegister = (Button)findViewById(R.id.loginActivity_button_register);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Acc_name = editTextAcc_name.getText().toString().trim();
                final String Acc_pwd = editTextAcc_pwd.getText().toString().trim();
                if(Acc_name == null || Acc_name.equals("")){
                    Toast.makeText(LoginActivity.this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
                    return;

                }else if(Acc_pwd == null || Acc_pwd.equals("")){
                    Toast.makeText(LoginActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    new AsyncTask() {
                        @Override
                        protected void onProgressUpdate(Object[] values) {
                            super.onProgressUpdate(values);
                            textViewProgress.setText(values[0].toString());
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            Toast.makeText(LoginActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
                            if (o.toString().contains("登录成功")) {//登录成功关闭登录界面
                                Intent intent = getIntent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("result", true);
                                intent.putExtras(bundle);
                                setResult(0x01, intent);
                                LoginActivity.this.finish();
                            }
                        }

                        @Override
                        protected Object doInBackground(Object[] params) {
                            HashMap<String, String> parameters = new HashMap<String, String>();
                            parameters.put("action", "login");
                            parameters.put("Acc_name", Acc_name);
                            parameters.put("Acc_pwd", Acc_pwd);
                            publishProgress("登录中...");
                            String json = sender.executePostByUsual(MainActivity.accountsActionURL, parameters);
                            try {
                                JSONObject jsonObject = new JSONObject(json.toString());
                                String result = jsonObject.get("result").toString().trim();
                                String Acc_ID = jsonObject.get("Acc_ID").toString().trim();
                                if (result.equals("true")) {//登录成功
                                    String accInfo = sender.executeGet(MainActivity.accountsActionURL + "?action=getAccInfoById&Acc_ID=" + Acc_ID);
                                    JSONObject accInfoObject = new JSONObject(accInfo);
                                    if (accInfo.startsWith("{")) {
                                        accounts.set_id(Long.parseLong(accInfoObject.get(Accounts.BASE_ACCOUNT_PHYSICAL_ID).toString()));
                                        accounts.setAcc_addTime(accInfoObject.get(Accounts.BASE_ACCOUNT_ADD_TIME).toString());
                                        accounts.setAcc_atn(Integer.parseInt(accInfoObject.get(Accounts.BASE_ACCOUNT_ATTENTION_COUNT).toString()));
                                        accounts.setAcc_atnd(Integer.parseInt(accInfoObject.get(Accounts.BASE_ACCOUNT_ATTENED_COUNT).toString()));
                                        accounts.setAcc_ID(accInfoObject.get(Accounts.BASE_ACCOUNT_ID).toString());
                                        accounts.setAcc_loc(accInfoObject.get(Accounts.BASE_ACCOUNT_LOCATION).toString());
                                        accounts.setAcc_lvl(Integer.parseInt(accInfoObject.get(Accounts.BASE_ACCOUNT_LEVEL).toString()));
                                        accounts.setAcc_name(accInfoObject.get(Accounts.BASE_ACCOUNT_NAME).toString());
                                        accounts.setAcc_name2(accInfoObject.get(Accounts.BASE_ACCOUNT_NAME2).toString());
                                        accounts.setAcc_no(accInfoObject.get(Accounts.BASE_ACCOUNT_NO).toString());
                                        accounts.setAcc_pub(Integer.parseInt(accInfoObject.get(Accounts.BASE_ACCOUNT_PUBLISH).toString()));
                                        accounts.setAcc_pwd(accInfoObject.get(Accounts.BASE_ACCOUNT_PASSWORD).toString());
                                        accounts.setAcc_ryb(Integer.parseInt(accInfoObject.get(Accounts.BASE_ACCOUNT_RYB).toString()));
                                        accounts.setAcc_sex(accInfoObject.get(Accounts.BASE_ACCOUNT_SEX).toString());
                                        accounts.setAcc_shop(Boolean.parseBoolean(accInfoObject.get(Accounts.BASE_ACCOUNT_IS_SHOP_OWNER).toString()));
                                        accounts.setAcc_tel(accInfoObject.get(Accounts.BASE_ACCOUNT_TELEPHONE).toString());
                                        MainActivity.isLogin = true;
                                        database.execSQL("delete from " + Accounts.BASE_TABLE_NAME + "");
                                        int shop = accounts.isAcc_shop() ? 0 : 1;
                                        database.execSQL("insert into " + Accounts.BASE_TABLE_NAME + " values(" + accounts.get_id() + ", '" + accounts.getAcc_ID() + "', '" + accounts.getAcc_name() + "', '" + accounts.getAcc_pwd() + "', '" + accounts.getAcc_sex() + "', '" + accounts.getAcc_loc() + "', " + accounts.getAcc_lvl() + ", " + accounts.getAcc_ryb() + ", " + shop + ", " + accounts.getAcc_atn() + ", " + accounts.getAcc_atnd() + ", " + accounts.getAcc_pub() + ", '" + accounts.getAcc_no() + "', '" + accounts.getAcc_name2() + "', '" + accounts.getAcc_tel() + "', '" + accounts.getAcc_addTime() + "')");
                                        publishProgress("登录成功!账号信息同步完成!");
                                        return "登录成功!账号信息同步完成!";
                                    } else {
                                        publishProgress("登录成功!账号信息同步失败!");
                                        return "登录成功!账号信息同步失败!";
                                    }
                                } else {
                                    publishProgress("登录失败!");
                                    return "登录失败!";
                                }
                            } catch (JSONException e) {
                                return "登录失败!";
                            }
                        }
                    }.execute();
                }
            }
        });
        this.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
