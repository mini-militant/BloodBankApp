package com.example.shailesh.bloodbankapp.Facts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shailesh.bloodbankapp.R;

import java.util.ArrayList;

/**
 * Created by shailesh on 17/3/18.
 */

public class FactsFragment extends Fragment {
    ArrayList<FactsModel> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;
    String bloodFacts[]={"One of the principal signs of life for humans is blood pressure, this is the measure of pressure that circulating blood has on the walls of blood vessels. " +
            "Blood pressure is usually taken from a person’s upper arm. Although averages vary from person to person, a general human being is known to have a normal blood pressure of around 112/64 mmHg. High blood pressure can increase the risk of a stroke or heart attack."
            ,"There are strict rules that limit the number of people who can volunteer blood donations. These include screening processes that test for diseases that could be transmitted by a blood transfusion as well as ensuring recovery time for the donor’s body to replace its " +
            "own blood."
            ,"White blood cells are an important part of the body’s immune system. They defend against certain bacteria, viruses, cancer cells, infectious diseases and other unwanted materials.",
            "Red blood cells have the important job of carrying oxygen around the body. They also contain a protein called hemoglobin. Hemoglobin contains iron which combines with oxygen to give hemoglobin and our blood, a red color.",
            "Red blood cells develop in bone marrow and circulate in the body for around 120 days.",
            "Every blood donor is given a mini-physical, checking the donor's temperature, blood pressure, pulse and hemoglobin to ensure it is safe for the donor to give blood.",
            "Type O-negative blood (red cells) can be transfused to patients of all blood types. It is always in great demand and often in short supply."};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeList();
        getActivity().setTitle("7 Wonders of the Modern World");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listitems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<FactsModel> list;

        public MyAdapter(ArrayList<FactsModel> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.facts.setText(list.get(position).getTextResourceId());
            holder.likeImageView.setTag(R.drawable.ic_like);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView facts;

        public ImageView likeImageView;
        public ImageView shareImageView;

        public MyViewHolder(View v) {
            super(v);
            facts = (TextView) v.findViewById(R.id.TextFacts);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int id = (int)likeImageView.getTag();
                    if( id == R.drawable.ic_like){

                        likeImageView.setTag(R.drawable.ic_liked);
                        likeImageView.setImageResource(R.drawable.ic_liked);

                        Toast.makeText(getActivity()," Added to favourites",Toast.LENGTH_SHORT).show();

                    }else{

                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);
                        Toast.makeText(getActivity()," Removed from favourites",Toast.LENGTH_SHORT).show();


                    }

                }
            });



            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {






                   /* Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(coverImageView.getId())
                            + '/' + "drawable" + '/' + getResources().getResourceEntryName((int)coverImageView.getTag()));*/


                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    //shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));



                }
            });



        }
    }

    public void initializeList() {
        listitems.clear();

        for(int i =0;i<bloodFacts.length;i++){


            FactsModel item = new FactsModel();
            item.setTextResourceId(bloodFacts[i]);
            item.setIsfav(0);
            item.setIsturned(0);
            listitems.add(item);

        }




    }
}
