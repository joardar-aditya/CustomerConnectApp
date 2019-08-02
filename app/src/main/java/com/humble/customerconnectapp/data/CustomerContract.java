package com.humble.customerconnectapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CustomerContract {
    //define the constants which are required in a Database
    private CustomerContract(){}
        public static final String CONTENT_AUTHORITY = "com.humble.customerconnectapp";
        public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
        public static final String PATH = "customer";

        public static abstract class CustomerEntry implements BaseColumns{
            public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH);
            public static final String TABLE_NAME = "Customers";
            public static final String CUSTOMER_NAME = "Name";
            public static final String PHONE ="phone" ;
            public static final String EMAIL = "E_mail";
            public static final String LAST_VISITED = "Last_Visited";
            public static final String VISITED_FOR = "Last_Visited_Reason";
            public static final String AMOUNT_DUE_FOR_PAYMENT = "Amount_Due";


            //MIME TYPE
            public static final String CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/Customer";
            public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY +"/Customer";



        }




}
