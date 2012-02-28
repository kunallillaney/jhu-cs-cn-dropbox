package cn.dropbox.common.parser.api;

public interface XMLConstants {
    
    // FILE/DIRECTORY PUT/GET XML
    public static final String RESOURCE_UPLOAD_TAG = "ResourceUpload";
    public static final String RESOURCE_TAG = "Resource";
    public static final String RESOURCE_ATTR_CATEGORY = "category";
    public static final String RESOURCE_NAME_TAG = "ResourceName";
    public static final String RESOURCE_SIZE_TAG = "ResourceSize";
    public static final String RESOURCE_DATE_TAG = "ResourceDate";
    public static final String RESOURCE_DATE_YEAR_TAG = "year";
    public static final String RESOURCE_DATE_MONTH_TAG = "month";
    public static final String RESOURCE_DATE_DAY_TAG = "day";
    public static final String RESOURCE_DATE_HOUR_TAG = "hour";
    public static final String RESOURCE_DATE_MINUTE_TAG = "min";
    public static final String RESOURCE_DATE_SECOND_TAG = "sec";
    public static final String RESOURCE_TYPE_TAG = "ResourceType";
    public static final String RESOURCE_ENCODING_TAG = "ResourceEncoding";
    public static final String RESOURCE_CONTENT_TAG = "ResourceContent";
    
    // FILE GET XML Only
    public static final String RESOURCE_DOWNLOAD_TAG = "ResourceDownload";
    
    // OTHER
    public static final String ENCODING_TYPE = "Base64";
    
    // DIRECTORY GET (Directory Listing) Only
    public static final String RESOURCE_LIST_TAG = "ResourceList";
    public static final String RESOURCE_URL_TAG = "ResourceURL";
    public static final String RESOURCE_NUM_ITEMS = "ResourceNumItems";
    
}
