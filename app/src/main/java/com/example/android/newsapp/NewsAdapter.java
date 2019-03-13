package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }

    //Returns a list item view that display information about News at a given position in the list

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Check if there is an existing view called covertView that we can reuse, otherwise
        //otherwise, if convertView is null, inflate a new list item layout
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent, false);
        }

        //Find the News object at the given position in list
        News currentArticle = getItem(position);

        //Find the TextView with category ID and display it
        TextView category = convertView.findViewById(R.id.category);
        category.setText(currentArticle.getCategoryName());

        //Find the TextView with author ID and display it
        TextView author = convertView.findViewById(R.id.author);
        author.setText(currentArticle.getAuthor());

        //Find the TextView with title ID and display it
        TextView title = convertView.findViewById(R.id.web_title);
        title.setText(currentArticle.getTitle());

        //Find the TextView with date and time ID and display it
        TextView dateAndTime = convertView.findViewById(R.id.date);

        //Separate the date from hour to look more readable (before: "1999-10-12T15:00:42Z" ;
        //after: "1999-10-12T
        //        15:00:42Z" )
        String resultDate = currentArticle.getDateAndTime();
        StringBuilder articleDateAndTime = new StringBuilder().append(resultDate);
        articleDateAndTime.insert(articleDateAndTime.length()-9, "\n");
        dateAndTime.setText(articleDateAndTime);

        //Return the list item view
        return convertView;
    }
}