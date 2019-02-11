package com.example.rssprocessing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Article> articleArrayList;

    private boolean inItem, inTitle, inPubDate, inDescription, inLink;

    private StringBuilder sb;

    private RSSTask rssTask;

    ListView lvRss;

    TextView tvTitle;

    private final int DEFAULT_REQUEST_CODE = 0;

    String URL = "https://www.cbc.ca/cmlink/rss-sports";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        rssTask = new RSSTask();
        switch (item.getItemId()) {
            case R.id.sportsFeed:
                this.setTitle("Sports Feed");
                URL = "https://www.cbc.ca/cmlink/rss-sports";
                rssTask.RSS_URL = URL;
                rssTask.execute();
                return true;
            case R.id.gamesFeed:
                this.setTitle("Games Feed");
                URL = "https://www.gamespot.com/feeds/mashup/";
                rssTask.RSS_URL = URL;
                rssTask.execute();
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, DEFAULT_REQUEST_CODE);
                return true;
            case R.id.refreshFeed:
                rssTask.RSS_URL = URL;
                rssTask.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            rssTask.TitleFontSize = data.getIntExtra("titleSize", 20);
            rssTask.PubDateFontSize = data.getIntExtra("pubDateSize", 14);
            int titleColor = data.getIntExtra("titleColor", 0);
            int pubDateColor = data.getIntExtra("pubDateColor", 0);
            if(titleColor == 0)
            {
                rssTask.TitleColor = Color.BLACK;
            }
            else if (titleColor == 1)
            {
                rssTask.TitleColor = Color.RED;
            }
            else if (titleColor == 2)
            {
                rssTask.TitleColor = Color.GREEN;
            }

            if(pubDateColor == 0)
            {
                rssTask.PubDateColor = Color.BLACK;
            }
            else if (pubDateColor == 1)
            {
                rssTask.PubDateColor = Color.RED;
            }
            else if (pubDateColor == 2)
            {
                rssTask.PubDateColor = Color.GREEN;
            }
            rssTask.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Dwight", "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvRss = findViewById(R.id.lvRss);
        rssTask = new RSSTask();
        rssTask.execute();
    }

    public class Article {

        String title,description,date,link;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    class CustomAdapter extends BaseAdapter {
        Context c;
        ArrayList<Article> articleList;
        int titleFontSize, pubDateFontSize, titleFontColor, pubDateFontColor;
        public CustomAdapter(Context c, ArrayList<Article> articleList, int titleFontSize, int pubDateFontSize, int titleFontColor, int pubDateFontColor)
        {
            this.c = c;
            this.articleList = articleList;
            this.titleFontSize = titleFontSize;
            this.pubDateFontSize = pubDateFontSize;
            this.titleFontColor = titleFontColor;
            this.pubDateFontColor = pubDateFontColor;
        }

        @Override
        public int getCount() {
            return articleList.size();
        }

        @Override
        public Object getItem(int position) {
            return articleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView= LayoutInflater.from(c).inflate(R.layout.list_item, parent,false);
            }

            TextView titleTxt= (TextView) convertView.findViewById(R.id.list_item_title);
            TextView dateTxt= (TextView) convertView.findViewById(R.id.list_item_date);
            titleTxt.setTextSize(titleFontSize);
            dateTxt.setTextSize(pubDateFontSize);
            titleTxt.setTextColor(titleFontColor);
            dateTxt.setTextColor(pubDateFontColor);
            final Article article= (Article) this.getItem(position);

            titleTxt.setText(article.getTitle());
            dateTxt.setText(article.getDate());

            return convertView;
        }
    }

    class RSSTask extends AsyncTask<Void, Void, Void> {
        private String RSS_URL = "https://www.cbc.ca/cmlink/rss-sports";
        public int TitleFontSize = 20;
        public int PubDateFontSize = 14;
        public int TitleColor = Color.BLACK;
        public int PubDateColor = Color.BLACK;

        @Override
        protected void onPreExecute() {
            Log.d("Dwight", "OnPreExecute");
            super.onPreExecute();
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);
                TitleFontSize = sharedPreferences.getInt("titleSize", 20);
                PubDateFontSize =  sharedPreferences.getInt("pubDateSize", 14);
                int titleColor =  sharedPreferences.getInt("titleColor", 0);
                int pubDateColor =  sharedPreferences.getInt("pubDateColor", 0);
                if(titleColor == 0)
                {
                    TitleColor = Color.BLACK;
                }
                else if (titleColor == 1)
                {
                    TitleColor = Color.RED;
                }
                else if (titleColor == 2)
                {
                    TitleColor = Color.GREEN;
                }

                if(pubDateColor == 0)
                {
                    PubDateColor = Color.BLACK;
                }
                else if (pubDateColor == 1)
                {
                    PubDateColor = Color.RED;
                }
                else if (pubDateColor == 2)
                {
                    PubDateColor = Color.GREEN;
                }
            } catch (Exception e)
            {
                Log.d("Dwight", e.toString());
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("Dwight", "DoInBackground");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = null;

            try {
                sp = spf.newSAXParser();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            URL url = null;

            try {
                url = new URL(RSS_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            InputStream is = null;

            try {
                is = url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CBCHandler cbcHandler = new CBCHandler();
            try {
                sp.parse(is, cbcHandler);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "RSS Feed Fetched!", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
            lvRss.setAdapter(new CustomAdapter(MainActivity.this, articleArrayList, TitleFontSize, PubDateFontSize, TitleColor, PubDateColor));
            Log.d("Dwight", articleArrayList.toString());
            lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemTitle = articleArrayList.get(position).getTitle();
                    String itemPubDate = articleArrayList.get(position).getDate();
                    String itemDescription = articleArrayList.get(position).getDescription();
                    String itemLink = articleArrayList.get(position).getLink();

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("title", itemTitle);
                    intent.putExtra("pubDate", itemPubDate);
                    intent.putExtra("description", itemDescription);
                    intent.putExtra("link", itemLink);
                    startActivity(intent);
                }
            });
        }
    }

    class CBCHandler extends DefaultHandler {

        Article currentArticle;

        @Override
        public void startDocument() throws SAXException {
            Log.d("Dwight", "StartDocument");
            super.startDocument();
            articleArrayList = new ArrayList<Article>(50);
        }

        @Override
        public void endDocument() throws SAXException {
            Log.d("Dwight", "EndDocument");
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if(qName.equals("item"))
            {
                currentArticle = new Article();
                inItem = true;
            } else if (inItem && qName.equals("title"))
            {
                inTitle = true;
                sb = new StringBuilder(100);
            } else if (inItem && qName.equals("pubDate"))
            {
                inPubDate = true;
                sb = new StringBuilder(100);
            } else if (inItem && qName.equals("description"))
            {
                inDescription = true;
                sb = new StringBuilder(100);
            } else if (inItem && qName.equals("link"))
            {
                inLink = true;
                sb = new StringBuilder(100);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if(qName.equals("item"))
            {
                articleArrayList.add(currentArticle);
                inItem = false;
            } else if (inItem && qName.equals("title"))
            {
                inTitle = false;
                currentArticle.setTitle(sb.toString());
            } else if (inItem && qName.equals("pubDate"))
            {
                inPubDate = false;
                String dateFormat = new SimpleDateFormat("HH:MM a MM/dd/yyyy").format(new Date(sb.toString()));
                currentArticle.setDate(dateFormat);
            } else if (inItem && qName.equals("description"))
            {
                inDescription = false;
                currentArticle.setDescription(sb.toString());
            } else if (inItem && qName.equals("link"))
            {
                inLink = false;
                currentArticle.setLink(sb.toString());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            if(inTitle)
            {
                sb.append(ch,start,length);
            } else if (inPubDate)
            {
                sb.append(ch,start,length);
            } else if (inDescription)
            {
                sb.append(ch,start,length);
            } else if (inLink)
            {
                sb.append(ch,start,length);
            }
        }


    }
}
