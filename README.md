# Monzo Code Test for Android Engineers

<img src="The_Guardian.png" width="100%" />

## Instructions

A developer at your company has been working on an app. The app uses the Guardian's news API to fetch headlines and display them to the user. Users can favourite articles that they like.

You've inherited their unfinished project. It looks like the project might have some bugs, and the user interface definitely needs some work.

Your job is to add the missing features shown in the enclosed specification, fixing any bugs you might come across and prioritising as you see fit. Feel free to re-work any aspect of the codebase you're not happy with: you own this project now.

You should spend no more than 4 hours on the project - we know this isn't anywhere near enough time to complete the spec, so no pressure to build all the features or fix all the bugs! We're interested to see how far you get in that time and the decisions you make along the way. Please include a short note with your response, explaining:

- what were your priorities, and why?
- if you had another two days, what would you have tackled next?
- what would you change about the structure of the code?
- what bugs did you find but not fix?
- what would you change about the visual design of the app?



# Responses

Time spent : Between 3 and 4 hours. (+ 30min to write this doc)

### what were your priorities, and why?

- Analyze each files and find bugs by thinking about all the use cases and right them down on a paper.
- Fix the high priority bugs (crashes, incorrect behaviour)
- Add the missing features, we are a product company
- Improve the part of the code that could improve the performance of the app / make the app cleaner (MVI, Dagger ... )
- Move the API key into the property file
- Add tests to validate the work done and avoid regression
- Review the design to be as close as the one provided and improve the UI 
- Cleanup low priorities that could be done in few minutes (put strings in strings.xml, put dimensions in dimens.xml, put content descriptions in content_descriptions.xml, add few comments ...)

### what bugs did you find but not fix?
I did fix everything I found.
By analyzing the code and writing down on a paper all the bugs I found it helps me to fix them more quickly, more productively.

Here is the bugs I found in the original code source.
As I reviewed the architecture of the app, most of those files are not part of this app anymore.

#### ArticleAdapter
- Context should never be static, that lead to a memory leak.

```
class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Article> articles = new ArrayList<>();

    private static Context context;
```

**Fix** : I use GroupAdapter from the Groupie for the recycler's view adapter, then I did not need to deal with this bug.
If I would still use this adapter, to inflate the view in the method `onCreateViewHolder`, it's possible to get the context from the parent ViewGroup (first param) directly, then no need to store the context in the adapter.

- showArticles method only add articles to the current lists. Might not be what wished. The list should be clear() before adding all the new items.

```
 void showArticles(List<Article> articles) {
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }
```

**Fix** : I use GroupAdapter from the Groupie for the recycler's view adapter, then I did not need to deal with this bug.
If I would still use this adapter, I would use DiffUtil to only update the items that has been modified instead of modifying all the items in the list : 

```
 void showArticles(List<Article> articles) {
       diffCallback.compareLists(this.articles, articles)
       val differenceFound = DiffUtil.calculateDiff(diffCallback)
       this.articles.clear()
       this.articles.addAll(list)
       differenceFound.dispatchUpdatesTo(this)
    }
```


- In the ArticleViewHolder, bind method, no need to call ButterKnife.bind every time the method bind is called. In the constructor is enough.

```
        void bind(Article article) {
            ButterKnife.bind(this, itemView);
```

**Fix** : As am using Groupie to generate my adapter for the recyclerview, all those issues are fixed.
If I would still use this adapter, I would use kotlinX, so no need to use Butterknife.

#### ArticlesActivity
- onRefreshAction L73 : Not possible to do onNext() of a null value. It will throw an error during the runtime (onNext called with null. Null values are generally not allowed in 2.x operators and source)

```
    @Override
    public Observable<Object> onRefreshAction() {
        return Observable.create(emitter -> {
            swipeRefreshLayout.setOnRefreshListener(() -> emitter.onNext(null));
```

**Fix** : This part of the code is not needed anymore


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

**Fix** : Add HttpLoggingInterceptor only in Debug and load the API_KEY from the property file instead of the string file.

#### ArticlesPresenter


- Register method should have a @NonNull View. As the view can be null, super.register() will complain if the view is null as super.register takes a non null parameter.
- There is no error handling in the subscribe(). For instance, if there is an error, the refreshing animation will keep spinning
- subscribeOn(ioScheduler) should be outside of the switchMapSingle
```
    @Override
    public void register(View view) {
        super.register(view);
```

**Fix** : Use MVI architecture + use Kotlin with non null objects. Otherwise it's possible to use the @NonNull annotation.

#### String.xml
- Api_constants should be remove and add the key in the prop file instead.

```
    <string name="guardian_api_key">XXXXXXX</string> 
```

**Fix**:  Store value in a property file

### if you had another two days, what would you have tackled next?


- Add the missing unit and espresso test to cover all the use cases and validate the work done
- Improve the design of the List to follow exactly the one provided in the Sketch file
- GuardianService : change the value of the page-size given the strength of the internet connexion to avoid the user with bad connexion to wait a long time to have the list of posts.


###  and what would you change about the structure of the code

I already switched from MVP to MVI. I found MVI more easy to work with, cleaner, better to scale and easier to test
 as it does not have any dependencies on the view.

### what would you change about the visual design of the app?
- Display a message when there is an error when loading the articles (done)
- Display a message when the data are loading instead of having a blank page (done)
- Display the title of 2 lines. One list seems to short and it's not that great
- Make a row of the list bigger (bigger height) with an image in cover (full width) instead of just of small rounded image
- Being able to add from the favourites from the list of articles
- Being able to share an article on the article's detail
- Being able to filter the list by favourite / date instead of having different sections in one list.
- Inform the user that a favourite has been added to the top of the list (as it's not very clear), mainly when it's the first article added.



### BONUS


### Improvements already made

- Compress images
- Use annotation @Binds instead of @Provides when possible to have less generated files from Dagger2.
- Does not reload users already fetched from the server. Currently store in memory but could use room if we wish to have some data consistency and be able to use the app offline.
- Network : Cache the requests for 10 minutes in order to not remake the request to the server, then consume less battery and get the data faster.
- Added content descriptions for all the imageview and textView for the users with disability.

### Improvements to make in the future


- Network : Cache the requests **smarly** with a different expire time given the resource to access.
- Network : Add some paging on the resources that intent to return a big list of data. To avoid the user to wait too long and **use less memory/less battery**.
- Network : **Take in consideration the connexion speed** to load a different amount of data per request, and make sure the user does not wait too long to get the response of the request.
- Network :  use https://developers.google.com/protocol-buffers/ or https://google.github.io/flatbuffers/ to **transit smaller on-the-wire packet size, then make the requests faster**.
Benchmark of the performance : https://codeburst.io/json-vs-protocol-buffers-vs-flatbuffers-a4247f8bda6f
- Offline :  Usage of **Room** to store some data and have some consistency between sessions, also in order to preload some views and be able to use the app offline. (better UX)

- Network / Coroutine : Use coroutine instead of RxAndroid for the request calls. Coroutine is lighter, faster and consume less memory. Better use RxAndroid when we wish to have a reactive app (with Flowable for instance.) [benchmark available here](https://proandroiddev.com/kotlin-coroutines-vs-rxjava-an-initial-performance-test-68160cfc6723)

- Requests content : Populate object in the JSON instead of providing only the object's id and make another request to get the full object.
  [GraphQL](https://graphql.org/) proposes this feature, and then help reduce response size, then speed up the response time.

- App : Create a light version / less greedy of the app for country that does not have high quality android devices (such as africa or asia?)


- CI : Add a CI to build + run the unit tests after each commit / add a nightly build that will run all the tests (junit + espresso, as it's longer to run).


### Layouts decision

- activity_article_list is a ConstraintLayout, the most efficient layout for complex UI as it's flatten the view (no nested views).
- activity_article_detail is a CoordinatorLayout as it's the best for animation (such as fade in when scrolling) when scrolling.
- custom_favorite  uses a merge tag as it does not need to have a parent view. It's better for the performance and it won't add a nester view in the view that will include this custom_favorite
- item_article is a relativeLayout. Could use a ConstraintLayout in another context, but ConstraintLayout is less performant in a recyclerview as it takes longer to inflate ConstraintLayout because of all the constraints.
relativeLayout is then a good choice, mainly that there is no nested views.
- item_date is a TextView because that's enough for what's needed.