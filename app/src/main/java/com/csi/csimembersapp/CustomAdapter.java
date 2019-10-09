package com.csi.csimembersapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
// Recycler view adapter for View Registrations activity
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable {

    private List<RegData> regDataList;
    private List<RegData> regDataListFiltered;
    private ContactsAdapterListener listener;
    Context context;

    //searchview implementation
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    regDataListFiltered = regDataList;
                } else {
                    List<RegData> filteredList = new ArrayList<>();
                    for (RegData row : regDataList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)
                                || row.getEvent_name().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getEvent_name().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    regDataListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = regDataListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                regDataListFiltered = (ArrayList<RegData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, email, college, event;

        public MyViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.tv_name);
            email = view.findViewById(R.id.tv_email);
            college = view.findViewById(R.id.tv_college);
            event = view.findViewById(R.id.tv_event);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(regDataListFiltered.get(getAdapterPosition()));
                    //Intent intent = new Intent(context, RegDetails.class);

                }
            });
        }
    }

    public CustomAdapter(List<RegData> regDataList, Context context, ContactsAdapterListener listener){
        this.regDataList = regDataList;
        this.regDataListFiltered = regDataList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);

        MyViewHolder myViewHolder = (MyViewHolder)itemView.getTag();
        if (myViewHolder == null){
            myViewHolder = new MyViewHolder(itemView);
            itemView.setTag(myViewHolder);
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final RegData regData = regDataListFiltered.get(position);
        holder.name.setText(regData.getName());
        holder.event.setText(regData.getEvent_name());
        holder.email.setText(regData.getEmail());
        holder.college.setText(regData.getCollege());
    }

    @Override
    public int getItemCount() {
        if (regDataListFiltered!=null){
            return regDataListFiltered.size();
        }else {
            return 0;
        }

    }

    public interface ContactsAdapterListener {
        void onContactSelected(RegData contact);
    }
}
