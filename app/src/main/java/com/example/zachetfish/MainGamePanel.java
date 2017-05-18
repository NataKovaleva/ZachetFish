package com.example.zachetfish;

/**
 * Created by Наталья on 18.05.2017.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final String TAG= MainGamePanel.class.getSimpleName();
    private MainThread thread;
    private Fish fish;
    private FishAnimated Afish;

    public MainGamePanel(Context context) {
        super(context);
        // Добавляем этот класс, как содержащий функцию обратного
        // вызова для взаимодействия с событиями
        getHolder().addCallback(this);

        // создаем поток для игрового цикла

        this.Afish=new FishAnimated(
                BitmapFactory.decodeResource(getResources(), R.drawable.fish3)
                ,350,500// начальное положение
                ,63,79// ширина и высота спрайта
                ,5,8);// FPS и число кадров в анимации
        this.fish=new Fish(BitmapFactory.decodeResource(getResources(), R.drawable.fish2),350,500);

        thread = new MainThread(getHolder(), this);

        // делаем GamePanel focusable, чтобы она могла обрабатывать сообщения
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.thread.setRunning(true);
        this.thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //посылаем потоку команду на закрытие и дожидаемся,
        //пока поток не будет закрыт.
        boolean retry = true;
        while (retry) {
            try {
                this.thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // пытаемся снова остановить поток thread
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == 0) {
            this.fish.handleActionDown((int)event.getX(), (int)event.getY());
            if(event.getY() > (float)(this.getHeight() - 50)) {
                this.thread.setRunning(false);
                ((Activity)this.getContext()).finish();
            } else {
                Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
            }
        }

        if(event.getAction() == 2 && this.fish.isTouched()) {
            this.fish.setX((int)event.getX());
            this.fish.setY((int)event.getY());
        }

        if(event.getAction() == 1 && this.fish.isTouched()) {
            this.fish.setTouched(false);
        }

        return true;
       /* if(event.getAction()== MotionEvent.ACTION_DOWN){
// вызываем метод handleActionDown, куда передаем координаты касания
            fish.handleActionDown((int)event.getX(),(int)event.getY());

// если щелчок по нижней области экрана, то выходим
            if(event.getY()> getHeight()-50){
                thread.setRunning(false);
                ((Activity)getContext()).finish();
            }else{
                Log.d(TAG,"Coords: x="+ event.getX()+",y="+ event.getY());
            }
        }if(event.getAction()== MotionEvent.ACTION_MOVE){
// перемещение
            if(fish.isTouched()){
// робот находится в состоянии перетаскивания,
// поэтому изменяем его координаты
                fish.setX((int)event.getX());
                fish.setY((int)event.getY());
            }
        }if(event.getAction()== MotionEvent.ACTION_UP){
// Обрабатываем отпускание
            if(fish.isTouched()){
                fish.setTouched(false);
            }
        }
        return true;*/
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        this.Afish.draw(canvas);
        this.fish.draw(canvas);
    }
    public void update(){
        this.Afish.update(System.currentTimeMillis());
// проверяем столкновение с правой стеной
        if(this.fish.getX()+this.fish.getBitmap().getWidth()/2>= getWidth()){
            this.fish.getSpeed().toggleXDirection();
        }
// проверяем столкновение с левой стеной
        if( this.fish.getX()- this.fish.getBitmap().getWidth()/2<=0){
            this.fish.getSpeed().toggleXDirection();
        }
// проверяем столкновение с нижней стеной
        if(this.fish.getY()+ this.fish.getBitmap().getHeight()/2>= getHeight()){
            this.fish.getSpeed().toggleYDirection();
        }
// проверяем столкновение с верхней стеной
        if(this.fish.getY()- this.fish.getBitmap().getHeight()/2<=0){
            this.fish.getSpeed().toggleYDirection();
        }
// Обновляем координаты робота
        this.fish.update();
    }


}
