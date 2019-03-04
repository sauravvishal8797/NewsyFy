package com.example.sauravvishal8797.newsyfy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sauravvishal8797.newsyfy.R;
import com.example.sauravvishal8797.newsyfy.models.NewsArticleModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<NewsArticleModel> newsArticles;

    //on-click listener for listening to the adapter item click
    private View.OnClickListener onClickListener;

    public NewsAdapter(Context context, ArrayList<NewsArticleModel> newsArticles) {
        this.mContext = context;
        this.newsArticles = newsArticles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_article_item, viewGroup, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        NewsArticleModel article = newsArticles.get(i);
        viewHolder.articleTitleText.setText(article.getmTitle());
        viewHolder.articleSourceText.setText(article.getNewsSourceModel().getmName());
        String publishTimeDate = article.getmPublishTime().substring(0, article.getmPublishTime().indexOf("T"));
        viewHolder.articlePublishTimeText.setText(getFormattedPublishTime(publishTimeDate));

        //loading cover image using Picasso library
        Picasso.with(mContext).load(article.getmUrlToImage()).into(viewHolder.articleCoverImage);
        viewHolder.articleTitleText.setTag(article);
    }

    /**
     * Changes publish date to the format(Feb 23 2019)
     * @param date
     * @return formatted date String
     */
    public String getFormattedPublishTime(String date){
        String formattedPublishDate = " ";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat("MMM dd yyyy");
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);
            formattedPublishDate = requiredDateFormat.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedPublishDate;
    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
    }

    public void setOnClickListener(View.OnClickListener lis) {
        onClickListener = lis;
    }

    public void swapDataSet(ArrayList<NewsArticleModel> newDataSet){
        newsArticles = newDataSet;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView articleCoverImage;
        TextView articleTitleText;
        TextView articleSourceText;
        TextView articlePublishTimeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleCoverImage = itemView.findViewById(R.id.article_cover_image);
            articleTitleText = itemView.findViewById(R.id.article_title);
            articleSourceText = itemView.findViewById(R.id.article_source);
            articlePublishTimeText = itemView.findViewById(R.id.article_publish_time);
        }
    }
}