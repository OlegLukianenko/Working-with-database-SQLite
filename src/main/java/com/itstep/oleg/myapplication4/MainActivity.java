package com.itstep.oleg.myapplication4;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    static MySQLiteOpenHelper dbHelper;
    static SimpleCursorAdapter adapter;

    //Цвет фона не вибраного елемента
    private int nrmlColor = Color.rgb(0x32, 0x99, 0x90);
    // Цвет фона  вибраного елемента
    private int slctColor = Color.rgb(0xE2, 0xA7, 0x6F);
    //Индекс выбраного елемента
    private static int curItem=-1;
    //Ссылка на виджет текущего выбраного элемента списка
    private View curView = null;

    private static AlertDialog dialog;
    private static  View dialogView;
    private static AlertDialog.Builder builder;
    private static boolean editFlag = false;

    private static EditText nameId;
    private static EditText priceId;
    private static EditText weightId;
    Cursor C;
    private static int idItem;
    private static String nameItem ;
    private static double priceItem;
    private static int weightItem;

    private static int status=0;

    static String curName="";
    static String curPrice="";
    static String curWeight="";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(status==0) {
            dbHelper = new MySQLiteOpenHelper(this);
            this.dbHelper = new MySQLiteOpenHelper(this);
            //считываем данные из БД и заполняем ListView
            final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

            C = db.query(MySQLiteOpenHelper.tblNameProducts,
                    null, null, null, null, null, MySQLiteOpenHelper.colId);

            this.adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.my_list_item,
                    C,
                    new String[]
                            {
                                    MySQLiteOpenHelper.colProductName,
                                    MySQLiteOpenHelper.colProductPrice,
                                    MySQLiteOpenHelper.colProductWeight
                            },
                    new int[]
                            {
                                    R.id.tvName,
                                    R.id.tvPrice,
                                    R.id.tvWeight
                            },
                    0) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    //---Подстветка отмеченного элемента списка----

                    if (position == MainActivity.curItem) {
                        view.setBackgroundColor(MainActivity.this.slctColor);
                        MainActivity.this.curView = view;
                    } else {
                        view.setBackgroundColor(MainActivity.this.nrmlColor);
                    }

                    return view;

                }
            };
        }

        if(status==1)
        {
            curName = nameId.getText().toString();
            curPrice = priceId.getText().toString();
            curWeight = weightId.getText().toString();
            CreateAlertDialog();
        }
        else if(status==2)
        {
            curName = nameId.getText().toString();
            curPrice = priceId.getText().toString();
            curWeight = weightId.getText().toString();

            CreateAlertDialog();

            nameId.setText(curName);
            priceId.setText(curPrice);
            weightId.setText(curWeight);
            curName ="";
            curPrice="";
            curWeight="";
        }

           final SQLiteDatabase db = dbHelper.getWritableDatabase();
           this.adapter.swapCursor(db.query(MySQLiteOpenHelper.tblNameProducts,
                   null, null, null, null, null, MySQLiteOpenHelper.colId)).close();

            ListView LV = (ListView) this.findViewById(R.id.lvMain);
            LV.setAdapter(this.adapter);

            // Назначение обработчика события по элементу списка
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Cursor C = adapter.getCursor();
                    idItem = C.getInt(C.getColumnIndex("_id"));
                    nameItem = C.getString(C.getColumnIndex("name"));
                    priceItem = C.getDouble(C.getColumnIndex("price"));
                    weightItem = C.getInt(C.getColumnIndex("weight"));

                    //снимаем выделение с предыдущего виделеного елемента
                    if (MainActivity.this.curItem != -1) {
                        MainActivity.this.curView.setBackgroundColor(MainActivity.this.nrmlColor);
                    }

                    //устанавливаем выделение на текущий элемент списка
                    MainActivity.this.curItem = position;
                    MainActivity.this.curView = view;
                    MainActivity.this.curView.setBackgroundColor(MainActivity.this.slctColor);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int menuId = item.getItemId();

        switch(menuId)
        {
            case R.id.action_add:
                status=1;
                CreateAlertDialog();
                return true;

            case R.id.action_del:
                if(curItem!=-1)
                {
                    final SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int count = db.delete("Products", "_id="+idItem, null);
                    Log.d("######", "Удалено строк : " + count);
                    MainActivity.this.curItem =-1;
                    MainActivity.this.adapter.swapCursor(db.query(MySQLiteOpenHelper.tblNameProducts,
                           null, null, null, null, null, MySQLiteOpenHelper.colId)).close();
                }
                return true;

            case R.id.action_edit:
                MainActivity.editFlag=true;
                if(curItem!=-1)
                {
                    CreateAlertDialog();
                    status=2;
                    nameId.setText(nameItem.toString());
                    priceId.setText( String.valueOf(priceItem));
                    weightId.setText( String.valueOf(weightItem));
                }
                else
                    Toast.makeText(MainActivity.this, "Выберите елемент списка", Toast.LENGTH_SHORT).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void CreateAlertDialog()
    {
        if(editFlag!=true)
        {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
            builder.setTitle("Добавить");

            DialogInterface.OnClickListener OCL2 =
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_NEGATIVE:
                                   status=3;
                                    break;
                                case DialogInterface.BUTTON_POSITIVE:
                                {
                                    if (nameId.getText().toString().equals("") || priceId.getText().toString().equals("") || weightId.getText().toString().equals("")) {
                                        Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                //Для формирования строки для добавления в БД:
                                                ContentValues row = new ContentValues();
                                                row.put("name", nameId.getText().toString());
                                                row.put("price", priceId.getText().toString());
                                                row.put("weight", weightId.getText().toString());

                                                long rowID = db.insert("Products", null, row);
                                                Log.d("######", "rowID = " + rowID);
                                        status=3;

                                        MainActivity.this.adapter.swapCursor(db.query(MySQLiteOpenHelper.tblNameProducts,
                                                null, null, null, null, null, MySQLiteOpenHelper.colId)).close();
                                    }
                                    break;
                                }
                            }
                        }
                    };

            builder.setNegativeButton("Отмена", OCL2);
            builder.setPositiveButton("Применить", OCL2);
            dialogView = this.getLayoutInflater().inflate(R.layout.activity_add, null, false);
            builder.setView(this.dialogView);//В классе обяьвить private View dialogView
            dialog = builder.create();
            dialog.show();

            nameId = (EditText) dialogView.findViewById(R.id.nameId);
            priceId = (EditText) dialogView.findViewById(R.id.priceId);
            weightId = (EditText) dialogView.findViewById(R.id.weightId);

            nameId.setText(curName);
            priceId.setText(curPrice);
            weightId.setText(curWeight);
            curName ="";
            curPrice="";
            curWeight="";

        }
        else
        {
            builder=new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);
            builder.setTitle("Редактировать");

            DialogInterface.OnClickListener OCL=
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,int which)
                        {
                            switch(which)
                            {
                                case DialogInterface.BUTTON_NEGATIVE :
                                    MainActivity.editFlag = false;
                                    status=3;
                                    break;
                                case DialogInterface.BUTTON_POSITIVE :
                                    nameId = (EditText) dialogView.findViewById(R.id.nameId);
                                    priceId = (EditText) dialogView.findViewById(R.id.priceId);
                                    weightId = (EditText) dialogView.findViewById(R.id.weightId);

                                    if (nameId.getText().toString().equals("") || priceId.getText().toString().equals("") || weightId.getText().toString().equals("")) {
                                        Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        ContentValues row = new ContentValues();
                                        row.put("name", nameId.getText().toString());
                                        row.put("price", priceId.getText().toString());
                                        row.put("weight", weightId.getText().toString());
                                        long num = db.update("Products", row, "_id=" + idItem, null);
                                        Log.d("######", "Обновлено строк" + num);

                                        MainActivity.editFlag=false;
                                        status=3;
                                        MainActivity.this.adapter.swapCursor(db.query(MySQLiteOpenHelper.tblNameProducts,
                                                null, null, null, null, null, MySQLiteOpenHelper.colId)).close();
                                    }
                                    break;
                            }
                        }
                    };
            builder.setNegativeButton("Отмена", OCL);
            builder.setPositiveButton("Применить", OCL);
            this.dialogView=this.getLayoutInflater().inflate(R.layout.activity_add,null,false);
            builder.setView(this.dialogView);//В классе обяьвить private View dialogView
            this.dialog = builder.create();
            dialog.show();

            nameId = (EditText) dialogView.findViewById(R.id.nameId);
            priceId = (EditText) dialogView.findViewById(R.id.priceId);
            weightId = (EditText) dialogView.findViewById(R.id.weightId);
        }
    }

}






































