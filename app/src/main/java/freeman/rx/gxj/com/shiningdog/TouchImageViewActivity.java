package freeman.rx.gxj.com.shiningdog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import freeman.rx.gxj.com.shiningdog.view.MineImageView;
import freeman.rx.gxj.com.shiningdog.view.PatImageView;
import freeman.rx.gxj.com.shiningdog.view.TouchImageView;

public class TouchImageViewActivity extends Activity {

	private Context mContext;

	private RelativeLayout content;
	private ImageView iv1;

	private Bitmap bp1;

	int widthScreen;
	int heightScreen;

	private LayoutInflater inflater;

	private TouchImageView tImage;
	private MineImageView mImage;
	private PatImageView patImageView;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main1);
		tImage = (TouchImageView) findViewById(R.id.tImage);
		tImage.setImageResource(R.mipmap.s_hx);

//		mImage = (MineImageView) findViewById(R.id.mImage);
//		mImage.setImageResource(R.mipmap.aa);


//		patImageView = (PatImageView) findViewById(R.id.patImageView);
//		mImage.setImageResource(R.mipmap.bb);
	}

}