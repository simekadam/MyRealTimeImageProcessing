/*
*  CameraPreview.java
*/
package my.project.MyRealTimeImageProcessing;

import java.io.IOException;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback, SurfaceTexture.OnFrameAvailableListener {
	private final SurfaceTexture mTexture;
	private Camera mCamera = null;
	private ImageView MyCameraPreview = null;
	private Bitmap bitmap = null;
	private int[] pixels = null;
	private byte[] FrameData = null;
	private int imageFormat;

 	private boolean bProcessing = false;
	
 	Handler mHandler = new Handler(Looper.getMainLooper());
	private final SurfaceHolder mHolder;
	private Canvas c;


	public CameraPreview(Context context) {
		this(context, null);
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);

		bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
		pixels = new int[1920 * 1080];
		mHolder = getHolder();
		mHolder.addCallback(this);
//		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		setZOrderOnTop(true);
		mTexture = new SurfaceTexture(1, true);
		mTexture.setOnFrameAvailableListener(this);

	}




	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1)
	{
//		// At preview mode, the frame data will push to here.
//		if (imageFormat == ImageFormat.NV21)
//        {
//			//We only accept the NV21(YUV420) format.
//			if ( !bProcessing )
//			{
//				FrameData = arg0;
//				mHandler.post(DoImageProcessing);
//			}
//        }

		if(mHolder == null){
		return;
	}


		try {
			synchronized (mHolder) {
				c = mHolder.lockCanvas();
				ImageProcessing(1920, 1080, arg0, pixels);
				bitmap.setPixels(pixels, 0, 1920, 0, 0, 1920, 1080);
				c.drawBitmap(bitmap, 0, 0, null);
				Log.d("SOMETHING", "Got Bitmap");
			}
		} finally {
			// do this in a finally so that if an exception is thrown
			// during the above, we don't leave the Surface in an
			// inconsistent state
			if (c != null) {
				mHolder.unlockCanvasAndPost(c);
			}
		}
	}
	
	public void onPause()
    {
    	mCamera.stopPreview();
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) 
	{
//	   synchronized (this) {
//		this.setWillNotDraw(false); // This allows us to make our own draw
//		// calls to this canvas
//		Log.e("Camera", "mCamera.setPreviewDisplay(holder)");
//
//		Camera.Parameters p = mCamera.getParameters();
//		p.setPreviewSize(1920, 1080);
//		p.setPreviewFormat(ImageFormat.NV21);
//		mCamera.setParameters(p);
//
//
//		//try { mCamera.setPreviewDisplay(holder); } catch (IOException e)
//		//  { Log.e("Camera", "mCamera.setPreviewDisplay(holder);"); }
//
//		   try {
//			   mCamera.setPreviewTexture(new SurfaceTexture(1));
//		   } catch (IOException e) {
//			   e.printStackTrace();
//		   }
//		   mCamera.startPreview();
//		   mCamera.setPreviewCallback(this);
//
//	}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) 
	{
		synchronized (this) {
			this.setWillNotDraw(true); // This allows us to make our own draw
			// calls to this canvas
			Log.e("Camera", "mCamera.setPreviewDisplay(holder)");
			mCamera = Camera.open();

			Camera.Parameters p = mCamera.getParameters();
			p.setPreviewSize(1920, 1080);
			p.setPreviewFormat(ImageFormat.NV21);
			mCamera.setParameters(p);


			//try { mCamera.setPreviewDisplay(holder); } catch (IOException e)
			//  { Log.e("Camera", "mCamera.setPreviewDisplay(holder);"); }
			try {
				mCamera.setPreviewTexture(mTexture);
				mCamera.setPreviewCallback(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.startPreview();

		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) 
	{
    	mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	//
	// Native JNI 
	//
    public native boolean ImageProcessing(int width, int height, 
    		byte[] NV21FrameData, int [] pixels);
    static 
    {
        System.loadLibrary("ImageProcessing");
    }

	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {

	}

//    private Runnable DoImageProcessing = new Runnable()
//    {
//        public void run()
//        {
//        	bProcessing = true;
//			ImageProcessing(PreviewSizeWidth, PreviewSizeHeight, FrameData, pixels);
//
//			bitmap.setPixels(pixels, 0, PreviewSizeWidth, 0, 0, PreviewSizeWidth, PreviewSizeHeight);
//			MyCameraPreview.setImageBitmap(bitmap);
//			bProcessing = false;
//        }
//    };

	public class MySurfaceTexture extends SurfaceTexture {

		public MySurfaceTexture(int texName) {
			super(texName);
		}





	}



   }
