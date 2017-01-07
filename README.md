# MvpPlus
A App Code style developed over from MVP, and made into a new plus style

## 一，背景
Android工程编码，直接沿用了MVC的编码结构，但是由于Activity等组件对View和Mode的高耦合，在编码过程中，经常破坏MVC的风格，使得代码结构和业务模块日益混杂，维护和复用成本越来越高。

## 二，解决思路
###为什么MVP成为主流

业界提出了MVP，MVVM等，结合项目实践，如果项目已经开发成熟，不再适合导入MVVM，且风格变化稍大，对项目重构风险较高；而MVP与MVC较为接近，学习成本和重构成本在可接受范围，因此采用MVP的实践成为主流。

###MVC和MVP

MVP 是从经典的模式MVC演变而来，它们的基本思想有相通的地方：

Controller/Presenter负责逻辑的处理，Model提供数据，View负 责显示。作为一种新的模式，MVP与MVC有着一个重大的区别：在MVP中View并不直接使用Model，它们之间的通信是通过Presenter (MVC中的Controller)来进行的，所有的交互都发生在Presenter内部，而在MVC中View会从直接Model中读取数据而不是通过 Controller。

在MVP里，Presenter完全把Model和View进行了分离，主要的程序逻辑在Presenter里实现。而且，Presenter与具体的View是没有直接关联的，而是通过定义好的接口进行交互，从而使得在变更View时候可以保持Presenter的不变，即重用！

##三，一个经典简易的MVP示例
###1 代码示例

建立bean

    public class UserBean {
	    private String mFirstName;
	    private String mLastName;
	    public UserBean(String firstName, String lastName) {
		    this. mFirstName = firstName;
		    this. mLastName = lastName;
	    }
	    public String getFirstName() {
	    	return mFirstName;
	    }
	    public String getLastName() {
	    	return mLastName;
	    }
    }
    


建立model接口（处理业务逻辑，这里指数据读写）

    public interface IUserModel {
	    void setID(int id);
	    void setFirstName(String firstName);
	    void setLastName(String lastName);
	    int getID();
	    UserBean load(int id);// 通过id读取user信息,返回一个UserBean
    }
    

建立view接口（更新ui中的view状态），这里列出需要操作当前view的方法
    
    public interface IUserView {
	    int getID();
	    String getFristName();
	    String getLastName();
	    void setFirstName(String firstName);
	    void setLastName(String lastName);
    }
    

建立presenter

主导器，通过iView和iModel接口操作model和view，activity可以把所有逻辑给presenter处理，这样java逻辑就从手机的activity中分离出来

    
    public class UserPresenter {
	    private IUserView mUserView;
	    private IUserModel mUserModel;
	    public UserPresenter(IUserView view) {
		    mUserView = view;
		    mUserModel = new UserModel();
	    }
	    public void saveUser( int id, String firstName, String lastName) {
		    mUserModel.setID(id);
		    mUserModel.setFirstName(firstName);
		    mUserModel.setLastName(lastName);
	    }
	    public void loadUser( int id) {
		    UserBean user = mUserModel.load(id);
		    mUserView.setFirstName(user.getFirstName()); // 通过调用IUserView的方法来更新显示
		    mUserView.setLastName(user.getLastName());
	    }
    }
    

### 2 思考升华

由上述示例可见，MVP也存在一些缺点，包括：

1，MVP的逻辑交互过于强调接口，如有业务增减，逻辑抽取和编码范围较大；

2，即使简单的数据交互也不得不按原来的编码流程增加逻辑；

3，View和界面组件维护在一起，对于Activity、Fragment等生命周期重视不够，很多时候形成了Activity组件和Presenter两大主持中心。

因此我们在实践中需要进行MVP的剪裁和扩展，形成了自己的MVPplus风格。

##四，MVPplus实践
示例工程：com.example.myapplication

备注：该工程从MVC结构开始编码，模拟实践过程中逐步迁移到MVP，其中迁移过程可以对比MainActivity到MainMVPActivity作为参考。

### 目录结构

**Mvpbase目录**

这里为mvp构建了组件基类，同时清晰的展示了MVPplus的代码结构，主要涉及

- Presenter -> Activity、Fragment族
- View -> ActivityView族
- Model -> controller-logic族
- Entity -> bean族

![](http://i.imgur.com/mZ9k4UL.png)

**1，Presenter基类**

	public abstract class BaseMvpActivity<V extends BaseActivityView, L extends BaseLogic> extends AppCompatActivity {
    	
    	protected V mActivityView;
    	protected L mLogic;
    	
    	@Override
    	protected void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	
	    	mActivityView = onCreateActivityView();
	    	if (null != mActivityView) {
	    		mActivityView.contentView = mActivityView.onInitView(this);
	    	}
	    	setContentView(mActivityView.contentView);
	    	mLogic = onCreateBaseLogic();
    	}
    	
    	protected abstract V onCreateActivityView();
    	
    	protected abstract L onCreateBaseLogic();
    	
    }


**核心作用**

- Presenter扩展界面组件
- 在presenter中将定义了两个泛型，分别为view基类和Logic基类
- 完成view和logic的定义接口，抽象给子类具体实现子view和子logic
- 完成view和Activity实现contentView的IOC控制

**2，Logic基类**

    
    public abstract class BaseLogic<T> {
    
    	/**
    	 * 观察者列表
    	 */
    	private List<T> observers = new ArrayList<T>();
    	
    	/**
    	 * @Description:添加观察者
    	 */
    	public synchronized void addObserver(T observer) {
    		if (!observers.contains(observer)) {
    			observers.add(observer);
    		}
    	}
    	
    	/**
    	 * 移除观察者
    	 */
    	public synchronized void removeObserver(T observer) {
    		if (null != observer && observers.contains(observer)) {
    			observers.remove(observer);
    		}
    	}
    }

**核心作用**

- 提供观察者模式，维护了presenters对logic的观察添加和移除
- 提供泛型由子类定义具体的观察者

**3，ActivityView基类**

    public abstract class BaseActivityView<T> {
    
	    public BaseActivityView() {
	    }
	    
	    protected View contentView;
	    
	    public View getContentView() {
	    	return contentView;
	    }
	    
	    protected abstract View onInitView(Context context);
	    
	    /**
	     * 观察者列表
	     */
	    private List<T> listeners = new ArrayList<T>();
	    
	    /**
	     * @Description:添加观察者
	     */
	    public synchronized void addListener(T observer) {
		    if (!listeners.contains(observer)) {
		    	listeners.add(observer);
		    }
	    }
	    
	    /**
	     * 移除观察者
	     */
	    public synchronized void removeListener(T observer) {
		    if (null != observer && listeners.contains(observer)) {
		    	listeners.remove(observer);
		    }
	    }
    }

**核心作用**

- 提供观察者模式，维护了presenters对view的监听添加和移除
- 提供泛型由子类定义具体的监听者
- 提供初始化方法，由子类实现一个具体的layout，一方面通过IOC供给presenter的setContentView方法，另一方面将layout抽取出Activity，在ActivityView类中独立维护

**4，BaseEntity基类**

    public class BaseEntity<T> {
	    public BaseEntity() {
	    }
	    public int rCode;
	    public String rMsg;
	    public T rContent;
    }

将具体的content通过泛型供子类定义

## 五，一个模块的初始化编码示例

示例模块：com.example.myapplication.presenter.MainMvpActivity

**1，创建MainView和MainListener**

- 在onInitView()中创建具体view并返回为presenter提供具体contentView
- 提供一个实例化方法，返回给presenter控制，这里采用单例工具类实现
- MainListener提供了主要的view业务接口，供view中的事件驱动

**备注：**

*由此，presenter对view的控制即可通过直接call成员方式，也可以通过callback方式，甚至通过eventbus消息方式，但是逻辑模块依然清晰明了。*

*当然，这也对Presenter的资源释放提出要求，需要做同生命周期的销毁工作，这也是良好的性能编码规范要求，详情参考presenter-Activity的onDestroy方法实现。*
    

    public interface MainListener {
    	void onClickListener(int id);
    }
    

/**
     * Created by Administrator on 2016/12/12.
     */
    
    public class MainView extends BaseActivityView<MainListener> {
	    private static final String TAG = MainView.class.getSimpleName();
	    private Context mContext;
	    public ImageView mImageView;
	    
	    public static MainView getInstance() {
	    	return Singlton.getInstance(MainView.class);
	    }
	    
	    @Override
	    protected View onInitView(Context context) {
		    View inflateView = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
		    mImageView = (ImageView) inflateView.findViewById(R.id.img_glide);
		    
		    //...
	    }
	    
	    void listenClick(View view) {
	    
	    }
	    
	    @Override
	    public void release() {
		    super.release();// 父类中移除观察者
		    Singlton.removeInstance(MainView.class);  // 子类从单例容器释放
	    
	    }
    }

**2，创建MainLogic和MainObserver**

- 提供一个实例化方法，返回给presenter控制，这里采用单例工具类实现
- 提供了主要的logic业务接口，供presenter调用
- MainObserver提供了主要数据回调接口，供logic业务的回调

----------


    public interface MainObserver {
	    void onLoginSuccess();
	    void onLoginFailed(CharSequence errorMsg);
	    void onRequestSuccess(ImageEntity entity);
    }


    public class MainLogic extends BaseLogic<MainObserver> {
	    
	    private static final String TAG = MainLogic.class.getSimpleName();
	    public static MainLogic getInstance() {
	    return Singlton.getInstance(MainLogic.class);
	    }
	    
	    public void commitLoginRequest(final String pUserName, final String pUserPwd) {
	    
	    new BackForeTask<MainLogic, String>() {
	    @Override
	    protected String doInBack() {
	    //
	    }
	    
	    @Override
	    protected void postedToFore(MainLogic outInstance, String postedValue) {
	    // 
	    }.start();
	    }
	    
	    	//...
    }

    
**3，创建MainMvpActivity**

- 实现View监听接口、实现Logic观察接口
- 实例化MainView、并添加监听
- 实例化MainLogic、并添加观察

----------
    
    public class MainMvpActivity extends BaseMvpActivity<MainView, MainLogic>
    implements MainObserver, MainListener {
		@Override
	    protected MainView onCreateActivityView() {
	        MainView instance = MainView.getInstance();
	        instance.addListener(this);
	        return instance;
	    }
	
	    @Override
	    protected MainLogic onCreateBaseLogic() {
	        MainLogic instance = MainLogic.getInstance();
	        instance.addObserver(this);
	        return instance;
	    }
	
	    @Override
	    public void onRequestSuccess(ImageEntity entity){
	        if (mActivityView != null) {
	            mActivityView.mImageView.setImageBitmap(entity.rContent);
	        }
	    }
	
	    @Override
	    public void onLoginSuccess() {
	        Log.d(TAG, "onLoginSuccess: currentThread-> " + Thread.currentThread().getName());
	        Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
	    }
	
	    @Override
	    public void onLoginFailed(CharSequence errorMsg) {
	        Log.d(TAG, "onLoginFailed: currentThread-> " + Thread.currentThread().getName());
	        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
	    }
		// ...
	}
    
    
## 六，一个交互逻辑编码示例

**示例：一个图片后台请求、返回前台加载显示**

1，MainView中点击事件驱动

    /**
         * BACK FORE 示例：异步加载大图片，返回bitmap对象，可以及时手工释放
         */
        inflateView.findViewById(R.id.btn_async_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				// mImageTask = new AsyncImageTask(MainMvpActivity.this);
				// mImageTask.start();
                listenClick(view);
            }
        });

2，MainMvpActivity中响应事件

从监听方法中获取事件，call起Logic接口

    @Override
    public void onClickListener(int id) {
        switch (id) {
            case R.id.btn_async_image:
				// mImageTask = new AsyncImageTask(MainMvpActivity.this);
				// mImageTask.start();
                MainLogic.getInstance().requestImage(this);
                break;
			// ...


3，MainLogic中执行异步任务回调

- 异步任务使用自定义封装的[轻量级]线程调度工具类，图片请求使用了封装的glide，参考项目widget目录。 
- 返回的数据，模拟使用ImageEntity封装，使用观察者接口进行回调，回到Presenter中调用view的接口显示图片。

----------



	public void requestImage(Context context) {
        AsyncImageTask mImageTask = new AsyncImageTask(this, context);
        mImageTask.start();
    }

    private static class AsyncImageTask extends BackForeTask<MainLogic, ImageEntity> {
        private Bitmap bitmap;
        private Context context;

        AsyncImageTask(MainLogic logic, Context context) {
            super(logic);
            this.context = context;
        }

        @Override
        protected ImageEntity doInBack() {
            Log.d(TAG, "AsyncImageTask: doInBack currentThread-> " + Thread.currentThread().getName());
            if (getOutInstance() != null) {

                Bitmap bitmap = new ImageLoader.Builder()
						// .imgView(getOutInstance().mActivityView.mImageView)   // your ImageView on the layout
                        .placeHolder(android.R.mipmap.sym_def_app_icon)
                        .url("http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1610/25/c47/28906783_1477398355944_mthumb.jpg")
						// .resId(R.drawable.ic_launcher)
                        .errorHolder(android.R.mipmap.sym_def_app_icon)
                        .transType(LoaderConfig.TRANS_ROUND) // your can choose a transType
						// .scaleType(LoaderConfig.SCALE_CENTER_CROP)
                        .build()    // build the ImageLoader instance
                        .loadAsBitmap(context, 500, 500);
                ImageEntity imageEntity = new ImageEntity(bitmap);
                imageEntity.rCode = 0;
                return imageEntity;
            }
            return null;
        }

        @Override
        protected void postedToFore(MainLogic outInstance, ImageEntity postedValue) {
            if (null != outInstance && null != postedValue) {
                Log.d(TAG, "AsyncImageTask: postedToFore currentThread-> " + Thread.currentThread().getName());
                bitmap = postedValue.rContent;
                if (bitmap != null && !bitmap.isRecycled()) {
                    outInstance.observeRequestImageSuccess(postedValue);
                }
            }
        }

        private void release(){
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }

    }

    void observeRequestImageSuccess(ImageEntity postedValue) {
        Log.d(TAG, "observeRequestImageSuccess(): rCode -> " + postedValue.rCode);
        List<MainObserver> tmpList = getObservers();
        for (MainObserver o : tmpList) {
            o.onRequestSuccess(postedValue);
        }
    }


4，MainMvpActivity处理回调数据

*此处为简便，MainMvpActivity得到回调数据，不再进一步回调到MainView中，实践中可以进一步在下发中使用callback，EventBus等进一步解耦。*


    @Override
    public void onRequestSuccess(ImageEntity entity) {
        Log.d(TAG, "onRequestSuccess: currentThread-> " + Thread.currentThread().getName());
    }


