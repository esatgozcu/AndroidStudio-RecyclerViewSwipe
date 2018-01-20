package com.example.esatgozcu.recyclerswipeviewkullanimi;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<String> countries =  new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder alertDialog;
    private EditText et_country;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDialog();
    }

    private void initViews(){
        //FloatingActionButton tanımlanıyor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        //RecyclerView tanımlanıyor
        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        //Eğer Recyclerview’in boyutunu değiştirmeyecekseniz performansı arttırmak için boyutunu sabitleyebilirsiniz
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //ArrayList adaptere atanıyor
        adapter = new DataAdapter(countries);
        //adapter recyclerView'e atanıyor
        recyclerView.setAdapter(adapter);
        //Verileri Ekliyoruz
        countries.add("TURKEY");
        countries.add("Russia");
        countries.add("United States of America");
        countries.add("Germany");
        countries.add("China");
        //Adaptore verinin değiştiğini bildirmek için notifyDataSetChanged methodunu kullanıyoruz.
        adapter.notifyDataSetChanged();
        initSwipe();


    }
    private void initSwipe(){

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //Listedeki Item'ları kaydırdığımız zaman hangi işlem gerçekleşecek ise onSwiped methodunda uyguluyoruz
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                //Hangi Item kaydırıldıysa position değişkenine Adapterdeki değeri atanıyor.
                int position = viewHolder.getAdapterPosition();

                //Eğer sola kaydırırsak
                if (direction == ItemTouchHelper.LEFT)
                {
                    //adapterden position değişkenine atanmış değeri kaldırıyoruz
                    adapter.removeItem(position);
                }
                else
                {
                    //Eğer sağa kaydırırsak
                    removeView();
                    edit_position = position;
                    alertDialog.setTitle("Edit Country");
                    et_country.setText(countries.get(position));
                    alertDialog.show();
                }
            }

            //Listedeki Item'ları kaydırdığımız zaman arka planın rengini ve hangi iconu alacağınıda onChildDraw() methodunda belirtiyoruz
            //Çizeceğimiz dikdörgenin koordinatlarını getTop(),getBottom(),getRight(),getLeft() methodların ile elde ediyoruz
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;


                    if(dX > 0){
                        //Sağa kaydırılmış ise arka planı ayarlıyoruz
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        //Sola kaydırılmış ise arka planı ayarlıyoruz
                        //Dikdörtgenin rengini ayarılıyoruz
                        p.setColor(Color.parseColor("#D32F2F"));
                        //Dikdörgenin Koordinatalarını ayarlıyoruz
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        //Dikdörgeni çiziyoruz
                        c.drawRect(background,p);
                        //iconu ayarlıyoruz
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        //iconun yerini ayarlıyoruz
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        //iconu çizdiriyoruz
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }
    private void initDialog(){
        //RecyclerView'deki Item'ları sağ sürüklediğimde ekleme yapacağımızda karşımıza çıkacak AlertDialog penceresini düzenliyoruz
        alertDialog = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eğer add değişkeni true olduysa yani float butona tıkladığımızda veya sağ sürükleme yaptığımızda
                //if bloğu çalışacak
                if(add){
                    //Save tuşuna basılırsa..
                    add =false;
                    adapter.addItem(et_country.getText().toString());
                    dialog.dismiss();
                } else {
                    countries.set(edit_position,et_country.getText().toString());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });
        et_country = (EditText)view.findViewById(R.id.et_country);
    }
    @Override
    public void onClick(View v) {

        //FloatAction Butona Tıklandığında..
        switch (v.getId()){
            case R.id.fab:
                removeView();
                add = true;
                alertDialog.setTitle("Add Country");
                et_country.setText("");
                alertDialog.show();
                break;
        }
    }
}
