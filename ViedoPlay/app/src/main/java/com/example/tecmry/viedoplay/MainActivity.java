package com.example.tecmry.viedoplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.show.api.ShowApiRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private RecyclerView mRecyclerView;
    private ItemAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SurfaceView surfaceView;
    private ProgressBar progressBar;
    private ImageView Iv_start;
    private Toolbar toolbar;
    private CircleView Cv_userimage;
    private List<ItmeNews> newslist = new ArrayList<>();
    private String content;
    public String url;
    private int BACK_PROCESS = 1;
    private int RESULT_CODE= 3;
    private int process,total;
    private ItmeNews itmeNews;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getNews();

    }
    public void initView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.RV);
        toolbar = (Toolbar)findViewById(R.id.Tl_search);
        setSupportActionBar(toolbar);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ItemAdapter(newslist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter.setItemClickListner(new ItemAdapter.OnItemClickListner() {
            @Override
            public void OnItemClickListner(View view, final int position) {
                TextView textView = (TextView)view.findViewById(R.id.TV_username);
                 Iv_start = (ImageView)view.findViewById(R.id.Iv_Button);
                surfaceView = (SurfaceView)view.findViewById(R.id.surfaceView);
                progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
                Cv_userimage = (CircleView)view.findViewById(R.id.IV_userImage);
                switch (view.getId()){
                    case R.id.TV_username:
                      //  startActivity(new Intent(MainActivity.this,Video.class));
                        break;
                    case R.id.Iv_Button:
                        Intent intent=new Intent(MainActivity.this,Video.class);
                        intent.putExtra("video_url", newslist.get(position).getVideo_url());
                        intent.putExtra("process",newslist.get(position).getProcess());
                        itmeNews.setProcess(process);
                        startActivityForResult(intent,1);
                        break;
                    case R.id.IV_userImage:
                        Toast.makeText(MainActivity.this,"你点击了头像",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("输入你想要的");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                content = query;
                System.out.println(content);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
    public void getNews(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String appid="38541";//要替换成自己的
                String secret="93abdc1f737046139b230433ca7efe18";//要替换成自己的
                final String res=new ShowApiRequest("http://route.showapi.com/255-1",appid,secret)
                        .addTextPara("type","41")
                        .addTextPara("page","1")
                        .post();
                try {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(res);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int responsecod = jsonObject.getInt("showapi_res_code");
                    System.out.println(responsecod);
                    if (responsecod==0){
                        JSONObject body = jsonObject.getJSONObject("showapi_res_body");
                        JSONObject pagebean = body.getJSONObject("pagebean");
                        int num = pagebean.getInt("allNum");
                        int a = num%20;
                        int c = num/20;
                        JSONArray contentlist = pagebean.getJSONArray("contentlist");
                        if (num<=20){
                            for (int i = 0;i<num;i++){
                               getItem(i,contentlist);
                            }

                        }else {
                           for (int i = 0;i<19;i++){
                               getItem(i,contentlist);
                           }
                        }

                    }else {
                        System.out.println("你的网络连接可能出现了问题");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public  void getItem(int i,JSONArray contentlist ){
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = contentlist.getJSONObject(i);
            itmeNews = new ItmeNews(1);
            itmeNews.setCreate_time(jsonObject1.getString("create_time"));
            itmeNews.setStory(jsonObject1.getString("text"));
            itmeNews.setLove_times("赞"+jsonObject1.getString("love"));
            itmeNews.setHate_times("不喜欢"+jsonObject1.getString("hate"));
            itmeNews.setUsername(jsonObject1.getString("name"));
            itmeNews.setVideo_url(jsonObject1.getString("video_uri"));
            itmeNews.setProfile_url(jsonObject1.getString("profile_image"));
            newslist.add(itmeNews);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(newslist.size()-1);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                System.out.println(resultCode==RESULT_OK);
                if (resultCode==RESULT_OK) {
                     process = data.getIntExtra("PROCESS", 0);
                     total = data.getIntExtra("TOTAL",0);
                    System.out.println("process"+process);
                    System.out.println("total"+total);
                  //  double a=process/total;
                  //  System.out.println(a);

                }
                System.out.println("能执行");
                break;

        }
    }


}
