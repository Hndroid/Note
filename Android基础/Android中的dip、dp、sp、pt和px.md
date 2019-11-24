### Android中的dip、dp、sp、pt、px、mm 和 in

> 来源：Google 官方

#### dip/dp

Density-independent Pixels - An abstract unit that is based on the physical density of the screen. These units are relative to a 160 dpi (dots per inch) screen, on which 1dp is roughly equal to 1px. When running on a higher density screen, the number of pixels used to draw 1dp is scaled up by a factor appropriate for the screen's dpi. Likewise, when on a lower density screen, the number of pixels used for 1dp is scaled down. The ratio of dp-to-pixel will change with the screen density, but not necessarily in direct proportion. Using dp units (instead of px units) is a simple solution to making the view dimensions in your layout resize properly for different screen densities. In other words, it provides consistency for the real-world sizes of your UI elements across different devices.

Density-independent Pixels（设备独立像素）.一个基于屏幕的物理像素的抽象单元。这些单位是相对于 160 dpi（每英寸点数）的屏幕，其中 1dp 大致等于 1px。在较高密度屏幕上运行时，用于绘制 1dp 的像素数按照适合屏幕 dpi 的系数放大。同样，当在较低密度屏幕上时，用于1dp的像素数按比例缩小。dp 与像素的比率将随着屏幕密度而变化，但不一定成正比。使用 dp 单位（而不是 px 单位）是一种简单的解决方案，可以使布局中的视图尺寸适当调整以适应不同的屏幕密度。换句话说，它为不同设备的UI元素的实际大小提供了一致性。

> dip 是 Android 早期的版本 xml 使用的像素单位，在现在推荐使用 dp。两者是同一个意义的不同名称。

---

#### sp

Scale-independent Pixels - This is like the dp unit, but it is also scaled by the user's font size preference. It is recommend you use this unit when specifying font sizes, so they will be adjusted for both the screen density and the user's preference.

Scale-independent Pixels（缩放独立像素） - 这与 dp 单位类似，但它也可以通过用户的字体大小首选项进行缩放。建议您在指定字体大小时使用此单位，以便根据屏幕密度和用户偏好调整它们。

---

#### pt

Points - 1/72 of an inch based on the physical size of the screen, assuming a 72dpi density screen.

Points（点数）. 基于屏幕的物理尺寸的1/72英寸，假设密度为72dpi的屏幕。

---

#### px

Pixels - Corresponds to actual pixels on the screen. This unit of measure is not recommended because the actual representation can vary across devices; each devices may have a different number of pixels per inch and may have more or fewer total pixels available on the screen.

Pixels（像素）.对应于屏幕上的实际像素。建议不要使用此计量单位，因为实际表示可能因设备而异;每个设备可以具有每英寸不同数量的像素，并且可以在屏幕上具有更多或更少的总像素。

---

#### mm

Millimeters - Based on the physical size of the screen.

Millimeters（毫米）.基于屏幕的物理尺寸。

---

#### in

Inches - Based on the physical size of the screen.

Inches（英寸）.基于屏幕的物理大小。

---

下面的代码是 dp、sp 转换为 px 的工具类，方便查找。

```Java
import android.content.Context;

public class DimenUtil {
    /**
     * dp、sp 转换为 px 的工具类
     *
     * @author fxsky 2012.11.12
     *
     */
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
```



