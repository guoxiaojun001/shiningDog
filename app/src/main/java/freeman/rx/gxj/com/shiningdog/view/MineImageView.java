package freeman.rx.gxj.com.shiningdog.view;

import android.app.Service;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MineImageView extends ImageView {

	private Matrix matrix = new Matrix();
	private Matrix currentMaritx = new Matrix();

	private boolean long_touch = false;
	private int mode = 0;// 用于标记模式
	private static final int DRAG = 1;// 拖动
	private static final int ZOOM = 2;// 放大
	private static final int ROTA = 3; // 旋转

	private PointF startPoint = new PointF();
	private PointF midPoint = new PointF();// 中心点

	private float startDis = 0;
	private float oldAngle = 1f;

	private Vibrator vibrator;
	private GestureDetector gdetector;

	boolean matrixCheck = false;

	/**
	 * 默认构造函数
	 *
	 * @param context
	 */
	public MineImageView(final Context context) {
		super(context);
		gdetector = new GestureDetector(context,
				new GestureDetector.OnGestureListener() {
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return true;
					}

					@Override
					public void onShowPress(MotionEvent e) {
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
											float distanceX, float distanceY) {
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						Log.e("onLongPress", "onLongPress");
						long_touch = true;
						vibrator = (Vibrator) context
								.getSystemService(Service.VIBRATOR_SERVICE);
						// 振动50ms，提示后续的操作将是旋转图片，而非缩放图片
						vibrator.vibrate(50);
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
										   float velocityX, float velocityY) {
						return true;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
				});
	}

	/**
	 * 该构造方法在静态引入XML文件中是必须的
	 *
	 * @param context
	 * @param paramAttributeSet
	 */
	public MineImageView(final Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		gdetector = new GestureDetector(context,
				new GestureDetector.OnGestureListener() {
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return true;
					}

					@Override
					public void onShowPress(MotionEvent e) {
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
											float distanceX, float distanceY) {
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						Log.e("onLongPress", "onLongPress");
						long_touch = true;
						vibrator = (Vibrator) context
								.getSystemService(Service.VIBRATOR_SERVICE);
						// 振动50ms，提示后续的操作将是旋转图片，而非缩放图片
						vibrator.vibrate(50);
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
										   float velocityX, float velocityY) {
						return true;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
				});
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:// 按下动作
				Log.e("ACTION_DOWN", "ACTION_DOWN");
				mode = DRAG;
				currentMaritx.set(this.getImageMatrix());// 记录ImageView当期的移动位置
				startPoint.set(event.getX(), event.getY());// 开始点
				long_touch = false;
				break;
			// 当屏幕上已经有触点（手指）,再有一个手指压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				// mode = ZOOM;
				startDis = distance(event);
				oldAngle = getDegree(event); // 计算第二个手指touch时，两指所形成的直线和x轴的角度
				if (startDis > 10f) {// 避免手指上有两个茧
					midPoint = mid(event);
					currentMaritx.set(this.getImageMatrix());// 记录当前的缩放倍数
					if (!long_touch) {
						mode = ZOOM;
					} else {
						mode = ROTA;
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:// 移动事件
				Log.e("ACTION_MOVE", "ACTION_MOVE");
				if (vibrator != null)
					vibrator.cancel();
				if (mode == DRAG) {// 图片拖动事件
					float dx = event.getX() - startPoint.x;// x轴移动距离
					float dy = event.getY() - startPoint.y;// y轴移动距离
					matrix.set(currentMaritx);// 在当前的位置基础上移动
					matrix.postTranslate(dx, dy);

				} else if (mode == ZOOM) {// 图片放大事件
					float endDis = distance(event);// 结束距离
					if (endDis > 10f) {
						float scale = endDis / startDis;// 放大倍数
						// Log.v("scale=", String.valueOf(scale));
						matrix.set(currentMaritx);
						matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					}
				} else if (mode == ROTA) {
					float newAngle = getDegree(event);
					matrix.set(currentMaritx);
					float degrees = newAngle - oldAngle;
					matrix.postRotate(degrees, midPoint.x, midPoint.y);
				}
				break;
			case MotionEvent.ACTION_UP:
				mode = 0;
				break;
			// 有手指离开屏幕，但屏幕还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
		}
		this.setImageMatrix(matrix);
		invalidate();
		gdetector.onTouchEvent(event);
		return true;
	}

	// 计算两个手指所形成的直线和x轴的角度
	private float getDegree(MotionEvent event) {
		return (float) (Math.atan((event.getY(1) - event.getY(0))
				/ (event.getX(1) - event.getX(0))) * 180f);
	}

	/**
	 * 两点之间的距离
	 *
	 * @param event
	 * @return
	 */
	private static float distance(MotionEvent event) {
		// 两根线的距离
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return (float)Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * 计算两点之间中心点的距离
	 *
	 * @param event
	 * @return
	 */
	private static PointF mid(MotionEvent event) {
		float midx = event.getX(0) + event.getX(1);
		float midy = event.getY(0) + event.getY(1);

		return new PointF(midx / 2, midy / 2);
	}

}