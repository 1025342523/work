package com.yifarj.yifadinghuobao.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yifarj.yifadinghuobao.R;

/**
 * @auther Czech.Yuan
 * @date 2017/1/4 11:25
 */
public class ProductPictureUtil {
    /**
     * 查询货品主图
     *//*
    public static ProductMainPictureEntity.ValueEntity fetchProductMainPicture(String url, int productId) {
        RequestParams params = new RequestParams();
        params.put("DataTypeName", "ProductImage");
        params.put("Body", "");
        params.put("Param", "[" + productId + "]");
        params.put("Token", AppInfoUtil.getToken());
        Requester.post(url + Constants.CUrl.FETCH, params, ProductMainPictureEntity.class, new RequestListener<ProductMainPictureEntity>() {
            @Override
            public void onSuccess(ProductMainPictureEntity entity) {
                super.onSuccess(entity);
                if (!entity.HasError) {
                    ProductMainPicture = entity.Value;
                } else {
                    ToastUtil.showToastShort("未获取到主图");
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMsg, Throwable throwable) {
                super.onFailure(errorCode, errorMsg, throwable);
                ToastUtil.showToastLong("请求失败,服务端异常或者网络连接异常");
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                super.onError(errorCode, errorMsg);
                ToastUtil.showToastLong("请求失败,服务端异常或者网络连接异常");
            }
        });
        return ProductMainPicture;
    }

    *//**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     *//*
    public static String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    *//**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *//*
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    *//**
     * 计算图片的缩放值
     *//*
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    *//**
     * bitmap转换成String
     *//*
    public static String bitmapToString(String filePath) {
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //1.5M的压缩后在100Kb以内，测试得值,压缩后的大小=94486字节,压缩后的大小=74473字节
        //这里的JPEG 如果换成PNG，那么压缩的就有600kB这样
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        Log.d("d", "压缩后的大小=" + b.length);
        return Base64.encodeToString(b, Base64.DEFAULT);
    }*/

    /**
     * 创建大图显示对话框
     */
    public static void createLargeImageDialog(Context context, String path) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_photo_entry, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.large_image);
        ImageView imageDelete = (ImageView) v.findViewById(R.id.iv_ImageDelete);
       /* if (path.contains("file") || path.contains("drawable")) {
            ImageLoaderHelper.displayImage(imageView, path);
//            Glide.with(context).load(path).error(R.mipmap.default_image).centerCrop().into(imageView);
        } else {
            ImageLoaderHelper.displayImage(imageView, AppInfoUtil.genPicUrl(path));
//            Glide.with(context).load(AppInfoUtil.genPicUrl(path)).error(R.mipmap.default_image).centerCrop().into(imageView);
        }*/
        Glide.with(context).load(path).error(R.drawable.default_image).into(imageView);
        //ImageLoaderHelper.displayImage(imageView, path);
        dialog.setView(v);
        dialog.show();
        imageDelete.setOnClickListener(v1 -> dialog.dismiss());
    }

}
