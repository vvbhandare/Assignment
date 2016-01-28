package desidime.com.floatingdesidimelauncher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import desidime.com.floatingdesidimelauncher.R;
import desidime.com.floatingdesidimelauncher.models.CopounInfo;

/**
 * Created by Vishal-TS on 26/01/16.
 */
public class CustomCopounAdapter  extends RecyclerView.Adapter<CustomCopounAdapter.CustomCopounViewHolder> {

    private ArrayList<CopounInfo> arrayListCopouns;
    private Context context;

    public CustomCopounAdapter(Context context, ArrayList<CopounInfo> arrayListCopouns) {
        this.arrayListCopouns = arrayListCopouns;
        this.context = context;
    }

    @Override
    public CustomCopounViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_copoun_row_item, null);

        CustomCopounViewHolder viewHolder = new CustomCopounViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomCopounViewHolder customViewHolder, int position) {
        CopounInfo copounInfo = arrayListCopouns.get(position);

        //Download image using picasso library
        Picasso.with(context).load(copounInfo.getCopounIcon())
                .error(R.drawable.desidime)
                .placeholder(R.drawable.desidime)
                .into(customViewHolder.imageView);

        //Setting text view title
        customViewHolder.textView.setText(copounInfo.getCopounName());
    }

    @Override
    public int getItemCount() {
        return (null != arrayListCopouns ? arrayListCopouns.size() : 0);
    }

    public class CustomCopounViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView imageView;
        protected TextView textView;

        public CustomCopounViewHolder(View view) {
            super(view);
            this.imageView = (CircleImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
        }
    }
}
