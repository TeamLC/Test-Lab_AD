# Test-Lab_admin
test admin page
- - -
# 로컬 방식의 ROOM DB
ROOM 3대 구성요소
-----------------
- DB
- 항목 = 테이블
- DAO = DB 조회 메소드 인터페이스
## 인프라 구축
    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"

    // optional - RxJava support for Room
    implementation "androidx.room:room-rxjava2:$room_version"

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation "androidx.room:room-guava:$room_version"

    // optional - Test helpers
    testImplementation "androidx.room:room-testing:$room_version"

    // LiveData
    implementation "androidx.lifecycle:lifecycle-livedata:2.2.0"
> room 라이브러리 패치

> livedata 패치

## 테이블(항목) 생성
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;

    // 생성자
    public DataModel(String title){
        this.title = title;
    }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /**
     * toString 재정의 -> 내용확인하기 위함
     * */
    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DataModel{");
        sb.append("id=").append(id);
        sb.append(", title=").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
> id 와 text 를 식별자로 선언

## 로컬 DB 생성
    @Database(entities = {DataModel.class}, version = 1, exportSchema = false)
    
    public abstract DataModelDAO dataModelDAO();
> dataModelDAO 객체가 DB 조회 오브젝트

## DAO 생성
    @Query("SELECT * FROM DataModel")
    LiveData<List<DataModel>> getAll();

    /**
     * id로 데이터 찾기
     * */
    @Query("SELECT * FROM DataModel WHERE id = :mId")
    DataModel getData(int mId);

    /**
     * Insert annotation -> 내용추가
     **/
    @Insert
    void insert(DataModel dataModel);

    /**
     * Delete annotation -> 내용 삭제
     **/
    @Delete
    void delete(DataModel dataModel);

    /**
     * id로 데이터를 찾고 입력받은 String 으로 타이틀을 변경
     **/
    @Query("UPDATE DataModel SET title =:mTitle  WHERE id =:mId ")
    void dataUpdate(int mId, String mTitle);


    /**
     *  Clear All -> 리스트 전체삭제
     **/
    @Query("DELETE FROM DataModel")
    void clearAll();
> 각 쿼리문 별로 메소드 처리

## 메인 액티비티
        db = Room.databaseBuilder(this, LocalDatabase.class, "test-db")
                .build();
> db 객체 생성

        db.dataModelDAO().getAll().observe(this, dataList -> {
            mResultTextView.setText(dataList.toString());
        });
> DB 변경마다 UI 갱신

        mAddButton.setOnClickListener(v -> {
            /**
             *  AsyncTask 생성자에 execute 로 DataModelDAO 객체를 던저준다.
             *  비동기 처리
             **/
            new DaoAsyncTask(db.dataModelDAO(),INSERT,0,"")
                    .execute(new DataModel(mAddEdit.getText().toString()));
        });
> 각 버튼별로 DAO 객체를 비동기 처리하기 위해 전달

    private static class DaoAsyncTask extends AsyncTask<DataModel,Void,Void> {
        private DataModelDAO mDataModelDAO;
        private String mType;
        private int mId;
        private String mTitle;

        private DaoAsyncTask (DataModelDAO dataModelDAO, String type, int id, String title) {
            this.mDataModelDAO = dataModelDAO;
            this.mType = type;
            this.mId = id;
            this.mTitle = title;
        }

        @Override
        protected Void doInBackground(DataModel... dataModels) {
            if(mType.equals("INSERT")){
                mDataModelDAO.insert(dataModels[0]);
            }
            else if(mType.equals("UPDATE")){
                if(mDataModelDAO.getData(mId) != null){
                    mDataModelDAO.dataUpdate(mId,mTitle);
                }
            }
            else if(mType.equals("DELETE")){
                if(mDataModelDAO.getData(mId) != null) {
                    mDataModelDAO.delete(mDataModelDAO.getData(mId));
                }
            }
            else if(mType.equals("CLEAR")){
                mDataModelDAO.clearAll();
            }
            return null;
        }
    }
> 전달된 DAO 객체 별로 DB 쿼리문 비동기 처리

# Heroku_postgres 연동
참고 레퍼런스를 우선 클론 코딩 해본뒤

해당 내용을 참고해 직접 작성한 API와 Heroku를 이용한 커스텀 연동 작업을 시행

## 인프라 구축
    <uses-permission android:name="android.permission.INTERNET"/>

    implementation 'com.google.code.gson:gson:2.6.2'
    implementation 'com.squareup.retrofit2:retrofit:2.1.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.1.0'
> HTTP 통신과 JSON 파싱 처리를 위한 퍼미션 그리고 라이브러리 패치

## JSON 입력 & 응답 처리
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    public Name(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Name(String name) {
        this.name = name;
    }
> 직렬화 클래스를 이용해 post 메소드 처리 관련 기능을 작성

## 엔드 포인트 처리
    @GET("read")
    Call<List<Name>> all();

    @POST("read")
    Call<Name> create(@Body Name name);
> get, post 각각의 메소드를 인터페이스로 작성

## 메인 액티비티
### heroku 앱, 서비스 인터페이스 지정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final HerokuService service = retrofit.create(HerokuService.class);
> GsonConverterFactory 를 통해 REST API 모든 값을 JSON으로 파싱
### GET 메소드 관련 처리
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Name>> createCall = service.all();
                createCall.enqueue(new Callback<List<Name>>() {
                    @Override
                    public void onResponse(Call<List<Name>> _, Response<List<Name>> resp) {
                        allNames.setText("ALL Names:\n");
                        for (Name b : resp.body()) {
                            allNames.append(b.name + "\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Name>> _, Throwable t) {
                        t.printStackTrace();
                        allNames.setText(t.getMessage());
                    }
                });
            }
        });
> 해당 버튼 이벤트 시 서비스 인터페이스를 통해 HTTP 요청이 이루어지고 응답 값을 출력

> 성공과 실패 상황 관련 메소드도 작성
### POST 메소드 관련 처리
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                Name name = new Name(random.nextInt(100),nameInput.getText().toString());
                Call<Name> createCall = service.create(name);
                createCall.enqueue(new Callback<Name>() {
                    @Override
                    public void onResponse(Call<Name> _, Response<Name> resp) {
                        Name newName = resp.body();
                        textView.setText("Created Name with ISBN: " + nameInput.getText().toString());
                    }

                    @Override
                    public void onFailure(Call<Name> _, Throwable t) {
                        t.printStackTrace();
                        textView.setText(t.getMessage());
                    }
                });
            }
        });
> 직렬화 클래스에 입력 값과 랜덤 값을 넘겨 마찬가지로 서비스 인터페이스를 통한 HTTP 요청 

> 해당 작업이 완료되면 텍스트 뷰로 입력 값을 렌더링
    
- - -
[ROOM 참고 레퍼런스](https://jroomstudio.tistory.com/29)

[Heroku_postgres 참고 레퍼런스](http://jkutner.github.io/2016/08/18/android-backend-api-heroku-retrofit.html)
