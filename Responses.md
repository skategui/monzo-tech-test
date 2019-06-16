# Technical test

Time spent : Between 3 and 4 hours. (+ 30min +/- to write this doc)

### what were your priorities, and why?

- Analyze each files and find bugs by thinking about all the use cases and right them down on a paper.
- Fix the high priority bugs (crashes)
- Add the missing features, we a a product company
- Improve the part of the code that could improve the performance of the app / make the app cleaner
- Review the design to be as close as the one provided and improve the UI 
- Add tests to validate the work done and avoid regression
- Move the API key into the property file
- Cleanup low priorities that could be done in few minutes (put strings in strings.xml, put dimentions in dimens.xml, put content descriptions in content_descriptions.xml ...)

### what bugs did you find but not fix?
I did fix everything I found.

Here is the bugs I found :

#### ArticleAdapter
- Context should never be static, that lead to a memory leak.

```
class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Article> articles = new ArrayList<>();

    private static Context context;
```

- showArticles method only add articles to the current lists. Might not be what wished. The list should be clear() before adding all the new items.

```
 void showArticles(List<Article> articles) {
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }
```


- In the ArticleViewHolder, bind method, no need to call ButterKnife.bind every time the method bind is called. In the constructor is enough.

```
        void bind(Article article) {
            ButterKnife.bind(this, itemView);
```

Fix : As am using Groupie to generate my adapter for the recyclerview, all those issues are fixed.
Groupie's adapter uses DiffUtil to update the list, that only update the items modified instead of all the list.

#### ArticlesActivity
- onRefreshAction L73 : Not possible to do onNext() of a null value. It will throw an error during the runtime (onNext called with null. Null values are generally not allowed in 2.x operators and source)

```
    @Override
    public Observable<Object> onRefreshAction() {
        return Observable.create(emitter -> {
            swipeRefreshLayout.setOnRefreshListener(() -> emitter.onNext(null));
```

Fix : This part of the code is not needed anymore


#### ArticlesModule
- In the createOkHttpClient method, use HttpLoggingInterceptor only in debug to avoid displaying the requests in the console in production.
- getAuthInterceptor method should read the API key from the buildConfig and not the strings.xml file, that is not very secure and could be found just by decompiling the API, in less than 3minutes.
```
HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(getAuthInterceptor(resources));
        clientBuilder.addInterceptor(loggingInterceptor);
        
        ...
        
        hb.add(HEADER_API_KEY, resources.getString(R.string.guardian_api_key));
```

Fix : Add HttpLoggingInterceptor only in Debug and load the API_KEY from the prop file instead of the stringx file.

#### ArticlesPresenter


- Register method should have a @NonNull View. As the view can be null, super.register() will complain if the view is null as super.register takes a non null parameter.
- There is no error handling in the subscribe(). For instance, if there is an error, the refreshing animation will keep spinning
- subscribeOn(ioScheduler) should be outside of the switchMapSingle
```
    @Override
    public void register(View view) {
        super.register(view);
```

FIX : Use MVI architecture + use Kotlin with non null object.

#### String.xml
- Api_constants should be remove and add the key in the prop instead

```
    <string name="guardian_api_key">enj8pstqu5yat6yesfsdmd39</string> 
```

FIX:  Store value in a prop file

### if you had another two days, what would you have tackled next and what would you change about the structure of the code?


#### GENERAL
- Add the missing unit and espresso test to cover all the use cases and validate the work done



#### GuardianService
    - Page-size should be a parameter, that change given the strength of the internet.
      This value will be different for people with a “bad” connexion to allow them still loading some news quickly, then having a better UX than having a loader .
    

### what would you change about the visual design of the app?
- Display a message when there is an error when loading the articles (done)
- Display a message when the data are loading instead of having a blank page (done)
- Display the title of 2 lines. One list seems to short and it's not that great
- Make a row of the list bigger (bigger height) with an image in cover (full width) instead of just of small rounded image
- Being able to add from the favourites from the list of articles
- Being able to share an article on the article's detail
- Being able to filter the list by favourite / date instead of having different sections in one list.
- Inform the user that a favourite has been added to the top of the list (as it's not very clear), mainly when it's the first article added.
