//package za.co.topitupkeyboard.adapters;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import za.co.topitupkeyboard.R;
//import za.co.topitupkeyboard.model.ActivityListModel;
//
//
//public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder>{
//
//    private Context context;
//    private List<ActivityListModel> activityListModels;
//
//    public TransactionsAdapter(Context context, List<ActivityListModel> activityListModels) {
//        this.context = context;
//        this.activityListModels = activityListModels;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_item_view,parent,false);
//
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
//        holder.date_balance_text.setText(activityListModels.get(position).getTxt1());
//        holder.expenseName.setText(activityListModels.get(position).getTxt2());
//        holder.expenseType.setText(activityListModels.get(position).getTxt3());
//        holder.price.setText(activityListModels.get(position).getTxt4());
//        holder.image.setImageResource(activityListModels.get(position).getImage1());
//
//        if (position == 0 || position == 2){
//            holder.date_balance_text.setVisibility(View.VISIBLE);
//        }else{
//            holder.date_balance_text.setVisibility(View.GONE);
//        }
//
//        if (position == 2){
//            holder.price.setTextColor(Color.parseColor("#2dbc61"));
//        }else{
//            holder.price.setTextColor(Color.parseColor("#fd5c63"));
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return activityListModels.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView date_balance_text,expenseName,expenseType,price;
//        ImageView image;
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            date_balance_text = itemView.findViewById(R.id.date_balance_text);
//            expenseName = itemView.findViewById(R.id.expenseName);
//            expenseType = itemView.findViewById(R.id.expenseType);
//            price = itemView.findViewById(R.id.price);
//            image = itemView.findViewById(R.id.image);
//        }
//    }
//}
