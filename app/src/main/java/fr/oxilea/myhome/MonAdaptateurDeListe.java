package fr.oxilea.myhome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MonAdaptateurDeListe extends ArrayAdapter<String> {

    // default available icons
    private Integer[] tab_images_pour_la_liste = {
            R.drawable.portail,
            R.drawable.prise,
            R.drawable.lampe,
            R.drawable.deficon,};


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        textView.setText(getItem(position));

        // get the icon from bdd
        DeviceBdd mySettingBdd = new DeviceBdd(getContext());
        mySettingBdd.open();
        ConnectedObject myObject= new ConnectedObject();

        myObject = mySettingBdd.getObjectWithId(position);
        mySettingBdd.close();

        if(convertView == null ) {
            // get icon id of the connected object
            int myIcon = Integer.parseInt(myObject.GetObjectIconType());

            if (myIcon < 3) {
                imageView.setImageResource(tab_images_pour_la_liste[myIcon]);
            }else{
                imageView.setImageResource(tab_images_pour_la_liste[3]);
            }
        }
        else
            rowView = (View)convertView;

        return rowView;
    }

    public MonAdaptateurDeListe(Context context, String[] values) {
        super(context, R.layout.rowlayout, values);
    }
}