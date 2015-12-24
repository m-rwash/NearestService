package com.mymap.nearestservice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final int   cafeCode        = 1;
    private final int   restaurantCode  = 2;
    private final int   parkCode        = 3;
    private final int   barCode         = 4;
    private final int   movieCode       = 5;
    private final int   bookStoreCode   = 6;
    private final int   atmCode         = 7;
    private final int   policeCode      = 8;
    private final int   hospitalCode    = 9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  startActivity(new Intent(MainActivity.this, MapActivity.class));

    }
    public void onClick(View view)
    {
        int id=view.getId();
        if(id == R.id.cafe)
        {
            Intent cafe = new Intent(MainActivity.this, MapActivity.class);
            cafe.putExtra("key",cafeCode);
            startActivity(cafe);
        }
        else if(id == R.id.restaurant)
        {
            Intent restaurant = new Intent(MainActivity.this, MapActivity.class);
            restaurant.putExtra("key",restaurantCode);
            startActivity(restaurant);
        }
        else if(id == R.id.park)
        {
            Intent park = new Intent(MainActivity.this, MapActivity.class);
            park.putExtra("key",parkCode);
            startActivity(park);
        }
        else if(id == R.id.bar)
        {
            Intent bar = new Intent(MainActivity.this, MapActivity.class);
            bar.putExtra("key",barCode);
            startActivity(bar);
        }
        else if(id ==R.id.movie)
        {
            Intent movie = new Intent(MainActivity.this, MapActivity.class);
            movie.putExtra("key", movieCode);
            startActivity(movie);
        }
        else if(id ==R.id.bookStore)
        {
            Intent bookStore = new Intent(MainActivity.this, MapActivity.class);
            bookStore.putExtra("key", bookStoreCode);
            startActivity(bookStore);
        }
        else if(id ==R.id.atm)
        {
            Intent atm = new Intent(MainActivity.this, MapActivity.class);
            atm.putExtra("key", atmCode);
            startActivity(atm);
        }
        else if(id ==R.id.police)
        {
            Intent police = new Intent(MainActivity.this, MapActivity.class);
            police.putExtra("key", policeCode);
            startActivity(police);
        }
        else if(id ==R.id.hospital)
        {
            Intent hospital = new Intent(MainActivity.this, MapActivity.class);
            hospital.putExtra("key", hospitalCode);
            startActivity(hospital);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
