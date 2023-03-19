package com.tw.speedbrowser.base.widget

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.tw.speedbrowser.R

/**
 *
 * @author: FreddyChen
 * @date  : 2022/02/07 06:12
 * @email : freddychencsc@gmail.com
 */
open class SleTextButton : AppCompatTextView, View.OnTouchListener {

    @Type
    private var type: Int = TYPE_NONE

    @Shape
    private var shape: Int = GradientDrawable.RECTANGLE
    private var innerRadius: Int = 0
    private var innerRadiusRatio: Float = 0f
    private var thickness: Int = 0
    private var thicknessRatio: Float = 0f
    private var normalBackgroundColor: Int = 0
    private var pressedBackgroundColor: Int = 0
    private var disabledBackgroundColor: Int = 0
    private var selectedBackgroundColor: Int = 0
    private var strokeWidth: Int = 0
    private var dashWidth: Float = 0f
    private var dashGap: Float = 0f
    private var normalStrokeColor: Int = 0
    private var pressedStrokeColor: Int = 0
    private var disabledStrokeColor: Int = 0
    private var selectedStrokeColor: Int = 0
    var normalTextColor: Int = 0
    var pressedTextColor: Int = 0
    private var disabledTextColor: Int = 0
    private var selectedTextColor: Int = 0
    private var cornersRadius: Float = 0f
    private var cornersTopLeftRadius: Float = 0f
    private var cornersTopRightRadius: Float = 0f
    private var cornersBottomLeftRadius: Float = 0f
    private var cornersBottomRightRadius: Float = 0f
    private var normalGradientColors: IntArray? = null
    private var pressedGradientColors: IntArray? = null
    private var disabledGradientColors: IntArray? = null
    private var selectedGradientColors: IntArray? = null
    private var gradientOrientation: Int = GRADIENT_ORIENTATION_TOP_BOTTOM

    // 预估强平价(%s)  USDT TEXT_DIRECTION_ANY_RTL 正常   我们%s  50 TEXT_DIRECTION_LTR 正常  rtlOnlyText = true 应对第一种情况  全是文本的
    private var rtlOnlyText: Boolean = false

    @GradientType
    private var gradientType: Int = GradientDrawable.LINEAR_GRADIENT
    private var gradientCenterX: Float = 0f
    private var gradientCenterY: Float = 0f
    private var gradientRadius: Float = 0f

    private var maskBackgroundColor: Int = DEFAULT_MASK_BACKGROUND_COLOR
    private var cancelOffset: Int = DEFAULT_CANCEL_OFFSET

    @TextStyle
    private var textStyle: Int = REGULAR

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context.obtainStyledAttributes(attrs, R.styleable.SleTextButton, defStyleAttr, 0).apply {
            type = getInt(R.styleable.SleTextButton_sle_type, TYPE_NONE)
            shape = getInt(R.styleable.SleTextButton_sle_shape, GradientDrawable.RECTANGLE)
            innerRadius = getDimensionPixelSize(R.styleable.SleTextButton_sle_innerRadius, 0)
            innerRadiusRatio = getFloat(R.styleable.SleTextButton_sle_innerRadiusRatio, 0f)
            thickness = getDimensionPixelSize(R.styleable.SleTextButton_sle_thickness, 0)
            thicknessRatio = getFloat(R.styleable.SleTextButton_sle_thicknessRatio, 0f)
            normalBackgroundColor =
                SlTextColorSetting.ColorSetting.getTextColor(context, getColor(R.styleable.SleTextButton_sle_normalBackgroundColor, 0))
            pressedBackgroundColor =
                SlTextColorSetting.ColorSetting.getTextColor(
                    context, getColor(
                        R.styleable.SleTextButton_sle_pressedBackgroundColor,
                        normalBackgroundColor
                    )
                )
            disabledBackgroundColor =
                SlTextColorSetting.ColorSetting.getTextColor(
                    context, getColor(
                        R.styleable.SleTextButton_sle_disabledBackgroundColor,
                        DEFAULT_DISABLE_BACKGROUND_COLOR
                    )
                )
            selectedBackgroundColor =
                SlTextColorSetting.ColorSetting.getTextColor(
                    context, getColor(
                        R.styleable.SleTextButton_sle_selectedBackgroundColor,
                        normalBackgroundColor
                    )
                )
            strokeWidth = getDimensionPixelSize(R.styleable.SleTextButton_sle_strokeWidth, 0)
            dashWidth = getDimension(R.styleable.SleTextButton_sle_dashWidth, 0f)
            dashGap = getDimension(R.styleable.SleTextButton_sle_dashGap, 0f)
            normalStrokeColor = getColor(R.styleable.SleTextButton_sle_normalStrokeColor, 0)
            pressedStrokeColor =
                getColor(R.styleable.SleTextButton_sle_pressedStrokeColor, normalStrokeColor)
            disabledStrokeColor =
                getColor(R.styleable.SleTextButton_sle_disabledStrokeColor, normalStrokeColor)
            val defaultColor = currentTextColor
            normalTextColor = SlTextColorSetting.ColorSetting.getNoChangeColor(
                context,
                getColor(R.styleable.SleTextButton_sle_normalTextColor, defaultColor), defaultColor
            )

            selectedStrokeColor =
                getColor(R.styleable.SleTextButton_sle_selectedStrokeColor, normalStrokeColor)

            pressedTextColor =
                SlTextColorSetting.ColorSetting.getTextColor(context, getColor(R.styleable.SleTextButton_sle_pressedTextColor, normalTextColor))
            disabledTextColor =
                SlTextColorSetting.ColorSetting.getTextColor(context, getColor(R.styleable.SleTextButton_sle_disabledTextColor, normalTextColor))
            selectedTextColor =
                SlTextColorSetting.ColorSetting.getTextColor(context, getColor(R.styleable.SleTextButton_sle_selectedTextColor, normalTextColor))
            cornersRadius = getDimension(R.styleable.SleTextButton_sle_cornersRadius, 0f)
            cornersTopLeftRadius =
                getDimension(R.styleable.SleTextButton_sle_cornersTopLeftRadius, 0f)
            cornersTopRightRadius =
                getDimension(R.styleable.SleTextButton_sle_cornersTopRightRadius, 0f)
            cornersBottomLeftRadius =
                getDimension(R.styleable.SleTextButton_sle_cornersBottomLeftRadius, 0f)
            cornersBottomRightRadius =
                getDimension(R.styleable.SleTextButton_sle_cornersBottomRightRadius, 0f)

            rtlOnlyText = getBoolean(R.styleable.SleTextButton_sle_rtlOnlyText, false);

            val normalGradientColorsResourceId =
                getResourceId(R.styleable.SleTextButton_sle_normalGradientColors, 0)
            if (normalGradientColorsResourceId != 0) {
                normalGradientColors = resources.getIntArray(normalGradientColorsResourceId)
            }
            val pressedGradientColorsResourceId =
                getResourceId(R.styleable.SleTextButton_sle_pressedGradientColors, 0)
            if (pressedGradientColorsResourceId != 0) {
                pressedGradientColors = resources.getIntArray(pressedGradientColorsResourceId)
            }
            val disabledGradientColorsResourceId =
                getResourceId(R.styleable.SleTextButton_sle_disabledGradientColors, 0)
            if (disabledGradientColorsResourceId != 0) {
                disabledGradientColors = resources.getIntArray(disabledGradientColorsResourceId)
            }
            val selectedGradientColorsResourceId =
                getResourceId(R.styleable.SleTextButton_sle_selectedGradientColors, 0)
            if (selectedGradientColorsResourceId != 0) {
                selectedGradientColors = resources.getIntArray(selectedGradientColorsResourceId)
            }
            gradientOrientation = getInt(
                R.styleable.SleTextButton_sle_gradientOrientation,
                GRADIENT_ORIENTATION_TOP_BOTTOM
            )
            gradientType =
                getInt(
                    R.styleable.SleTextButton_sle_gradientType,
                    GradientDrawable.LINEAR_GRADIENT
                )
            gradientCenterX = getDimension(R.styleable.SleTextButton_sle_gradientCenterX, 0f)
            gradientCenterY = getDimension(R.styleable.SleTextButton_sle_gradientCenterY, 0f)
            gradientRadius = getDimension(R.styleable.SleTextButton_sle_gradientRadius, 0f)
            maskBackgroundColor = SlTextColorSetting.ColorSetting.getTextColor(
                context, getColor(
                    R.styleable.SleTextButton_sle_maskBackgroundColor,
                    DEFAULT_MASK_BACKGROUND_COLOR
                )
            )
            cancelOffset = getDimensionPixelSize(
                R.styleable.SleTextButton_sle_cancelOffset,
                DEFAULT_CANCEL_OFFSET
            )
            textStyle = getInt(R.styleable.SleTextButton_sle_textStyle, REGULAR)
            if (isRtl(context)) {
                // TEXT_DIRECTION_ANY_RTL   我们50    我们%s   近%s天 7   近三十天
                textDirection = if (rtlOnlyText) {
                    TEXT_DIRECTION_ANY_RTL
                } else {
                    TEXT_DIRECTION_LTR
                }
            }
            recycle()
        }
        init()
    }

    //是否是RTl
    fun isRtl(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL;
    }

//    override fun onDraw(canvas: Canvas) {
//        val drawables = compoundDrawables
//        if (drawables.isNotEmpty()) {
//            val drawableLeft = drawables[0]
//            drawableLeft?.let {
//                val textWidth = paint.measureText(text.toString())
//                val drawableWidth = drawableLeft.intrinsicWidth
//                val drawablePadding = compoundDrawablePadding
//                val bodyWidth = textWidth + drawableWidth + drawablePadding
//                canvas.translate((width - bodyWidth) * 1.0f / 2, 0.0f)
//                gravity = Gravity.CENTER_VERTICAL
//            }
//        }
//        super.onDraw(canvas)
//    }

    private fun init() {
        initType()
        initTypeface(textStyle)
        setOnTouchListener(this)
    }

    fun setTextStyle(@TextStyle textStyle: Int) {
        initTypeface(textStyle)
    }

    private fun initType() {
        val normalDrawable =
            getDrawable(normalBackgroundColor, normalStrokeColor, normalGradientColors)
        var pressedDrawable: GradientDrawable? = null
        var disabledDrawable: GradientDrawable? = null
        val selectedDrawable: GradientDrawable?
        when (type) {
            TYPE_MASK -> {
                pressedDrawable = getDrawable(
                    normalBackgroundColor,
                    normalStrokeColor,
                    normalGradientColors
                ).apply {
                    colorFilter =
                        PorterDuffColorFilter(maskBackgroundColor, PorterDuff.Mode.SRC_ATOP)
                }
                disabledDrawable =
                    getDrawable(disabledBackgroundColor, disabledBackgroundColor)
            }
            TYPE_SELECTOR -> {
                pressedDrawable =
                    getDrawable(pressedBackgroundColor, pressedStrokeColor, pressedGradientColors)
                disabledDrawable = getDrawable(
                    disabledBackgroundColor,
                    disabledStrokeColor,
                    disabledGradientColors
                )
            }
        }
        selectedDrawable = getDrawable(
            selectedBackgroundColor,
            selectedStrokeColor,
            selectedGradientColors
        )
        setTextColor(if (isSelected) selectedTextColor else normalTextColor)
        background = StateListDrawable().apply {
            if (type != TYPE_NONE) {
                addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            }
            addState(intArrayOf(-android.R.attr.state_enabled), disabledDrawable)
            addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
            addState(intArrayOf(), normalDrawable)
        }
    }

    private fun initTypeface(textStyle: Int) {
        this.textStyle = textStyle

//        when (textStyle) {
//            BLACK -> {
//                typeface = FontUtils.getSansBlack(context)
//            }
//            BOLD -> {
//                typeface = FontUtils.getSansBold(context)
//            }
//            LIGHT -> {
//                typeface = FontUtils.getSansLight(context)
//            }
//            MEDIUM -> {
//                typeface = FontUtils.getSansMedium(context)
//            }
//            REGULAR -> {
//                typeface = FontUtils.getSansRegular(context)
//            }
//            THIN -> {
//                typeface = FontUtils.getSansThin(context)
//            }
//        }
    }

//    fun setType(@IntRange(from = TYPE_NONE.toLong(), to = TYPE_CHECKBOX.toLong()) type: Int) {
//        this.type = type
//        initType()
//    }

    private fun getDrawable(
        backgroundColor: Int,
        strokeColor: Int,
        gradientColors: IntArray? = null
    ): GradientDrawable {
        // 背景色相关
        val drawable = GradientDrawable()
        setupColor(drawable, backgroundColor)

        // 形状相关
        (drawable.mutate() as GradientDrawable).shape = shape
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.innerRadius = innerRadius
            if (innerRadiusRatio > 0f) {
                drawable.innerRadiusRatio = innerRadiusRatio
            }
            drawable.thickness = thickness
            if (thicknessRatio > 0f) {
                drawable.thicknessRatio = thicknessRatio
            }
        }

        // 描边相关
        setupStroke(drawable, strokeWidth, strokeColor, dashWidth, dashGap)

        // 圆角相关
        setupCornersRadius(
            drawable,
            cornersRadius,
            cornersTopLeftRadius,
            cornersTopRightRadius,
            cornersBottomRightRadius,
            cornersBottomLeftRadius
        )

        // 渐变相关
        (drawable.mutate() as GradientDrawable).gradientType = gradientType
        if (gradientCenterX != 0.0f || gradientCenterY != 0.0f) {
            (drawable.mutate() as GradientDrawable).setGradientCenter(
                gradientCenterX,
                gradientCenterY
            )
        }
        gradientColors?.let { colors ->
            (drawable.mutate() as GradientDrawable).colors = colors
        }
        var orientation: GradientDrawable.Orientation? = null
        when (gradientOrientation) {
            GRADIENT_ORIENTATION_TOP_BOTTOM -> {
                orientation = GradientDrawable.Orientation.TOP_BOTTOM
            }
            GRADIENT_ORIENTATION_TR_BL -> {
                orientation = GradientDrawable.Orientation.TR_BL
            }
            GRADIENT_ORIENTATION_RIGHT_LEFT -> {
                orientation = GradientDrawable.Orientation.RIGHT_LEFT
            }
            GRADIENT_ORIENTATION_BR_TL -> {
                orientation = GradientDrawable.Orientation.BR_TL
            }
            GRADIENT_ORIENTATION_BOTTOM_TOP -> {
                orientation = GradientDrawable.Orientation.BOTTOM_TOP
            }
            GRADIENT_ORIENTATION_BL_TR -> {
                orientation = GradientDrawable.Orientation.BL_TR
            }
            GRADIENT_ORIENTATION_LEFT_RIGHT -> {
                orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
            GRADIENT_ORIENTATION_TL_BR -> {
                drawable.orientation = GradientDrawable.Orientation.TL_BR
            }
        }
        orientation?.apply {
            (drawable.mutate() as GradientDrawable).orientation = this
        }
        return drawable
    }

    fun setTextColor(
        normalTextColor: Int = this.normalTextColor,
        pressedTextColor: Int = this.pressedTextColor,
        disabledTextColor: Int = this.disabledTextColor
    ) {
        if (normalTextColor != 0) setTextColor(normalTextColor)
        this.normalTextColor = normalTextColor
        this.pressedTextColor = pressedTextColor
        this.disabledTextColor = disabledTextColor
    }

    fun setCornersRadius(
        cornersRadius: Float = this.cornersRadius,
        cornersTopLeftRadius: Float = this.cornersTopLeftRadius,
        cornersTopRightRadius: Float = this.cornersTopRightRadius,
        cornersBottomRightRadius: Float = this.cornersBottomRightRadius,
        cornersBottomLeftRadius: Float = this.cornersBottomLeftRadius,
    ) {
        if (background !is StateListDrawable) return
        if (background.constantState !is DrawableContainer.DrawableContainerState) return
        val dcs: DrawableContainer.DrawableContainerState =
            background.constantState as DrawableContainer.DrawableContainerState
        val children = dcs.children
        if (children.isNullOrEmpty()) return
        this.cornersRadius = cornersRadius
        this.cornersTopLeftRadius = cornersTopLeftRadius
        this.cornersTopLeftRadius = cornersTopLeftRadius
        this.cornersBottomRightRadius = cornersBottomRightRadius
        this.cornersBottomLeftRadius = cornersBottomLeftRadius
        children.forEach continuing@{ drawable ->
            if (drawable !is GradientDrawable) return@continuing
            setupCornersRadius(
                drawable,
                cornersRadius,
                cornersTopLeftRadius,
                cornersTopRightRadius,
                cornersBottomRightRadius,
                cornersBottomLeftRadius
            )
        }
    }

    fun setStroke(
        strokeWidth: Int = this.strokeWidth,
        dashWidth: Float = this.dashWidth,
        dashGap: Float = this.dashGap,
        strokeColor: Int = this.normalStrokeColor,
    ) {
        if (background !is StateListDrawable) return
        if (background.constantState !is DrawableContainer.DrawableContainerState) return
        val dcs: DrawableContainer.DrawableContainerState =
            background.constantState as DrawableContainer.DrawableContainerState
        val children = dcs.children
        if (children.isNullOrEmpty()) return
        this.strokeWidth = strokeWidth
        this.dashWidth = dashWidth
        this.dashGap = dashGap
        this.normalStrokeColor = strokeColor
        children.forEach continuing@{ drawable ->
            if (drawable !is GradientDrawable) return@continuing
            setupStroke(drawable, strokeWidth, strokeColor, dashWidth, dashGap)
        }
    }

    private fun setupStroke(
        drawable: GradientDrawable,
        strokeWidth: Int,
        strokeColor: Int,
        dashWidth: Float,
        dashGap: Float
    ) {
        (drawable.mutate() as GradientDrawable).setStroke(
            strokeWidth,
            strokeColor,
            dashWidth,
            dashGap
        )
    }

    private fun setupCornersRadius(
        drawable: GradientDrawable,
        cornersRadius: Float,
        cornersTopLeftRadius: Float,
        cornersTopRightRadius: Float,
        cornersBottomRightRadius: Float,
        cornersBottomLeftRadius: Float
    ) {
        if (cornersRadius > 0f) {
            (drawable.mutate() as GradientDrawable).cornerRadius = cornersRadius
        } else {
            // 指定4个角点中每个角点的半径。对于每个角点，数组
            // 包含两个值，X半径，Y半径
            // 顺序为左上角、右上角、右下角、左下角
            (drawable.mutate() as GradientDrawable).cornerRadii = floatArrayOf(
                cornersTopLeftRadius,
                cornersTopLeftRadius,

                cornersTopRightRadius,
                cornersTopRightRadius,

                cornersBottomRightRadius,
                cornersBottomRightRadius,

                cornersBottomLeftRadius,
                cornersBottomLeftRadius,
            )
        }
    }

    /**
     * 由于无法获取对应的state，所以设置背景色的话会把所有状态drawable的背景色都覆盖掉
     */
    fun setColor(backgroundColor: Int) {
        if (background !is StateListDrawable) return
        if (background.constantState !is DrawableContainer.DrawableContainerState) return
        val dcs: DrawableContainer.DrawableContainerState =
            background.constantState as DrawableContainer.DrawableContainerState
        val children = dcs.children
        if (children.isNullOrEmpty()) return
        this.normalBackgroundColor = backgroundColor
        this.pressedBackgroundColor = backgroundColor
        this.disabledBackgroundColor = backgroundColor
        this.selectedBackgroundColor = backgroundColor
        children.forEach continuing@{ drawable ->
            if (drawable !is GradientDrawable) return@continuing
            setupColor(drawable, backgroundColor)
        }
    }

    private fun setupColor(drawable: GradientDrawable, backgroundColor: Int) {
        if (backgroundColor != 0) {
            (drawable.mutate() as GradientDrawable).setColor(backgroundColor)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (normalTextColor == 0 || disabledTextColor == 0) return
        if (enabled) {
            setTextColor(if (isSelected) selectedTextColor else normalTextColor)
        } else {
            setTextColor(disabledTextColor)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        onTouchCallback?.invoke(v, event)
        if (type != TYPE_SELECTOR) {
            return false
        }

        if (!isEnabled || !isClickable) {
            return false
        }

        if (normalTextColor == 0 || pressedTextColor == 0 || (normalTextColor == pressedTextColor)) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setTextColor(pressedTextColor)
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x
                val currentY = event.y
                if (currentX < (0 - DEFAULT_CANCEL_OFFSET) || currentX > (width + DEFAULT_CANCEL_OFFSET) || currentY < (0 - DEFAULT_CANCEL_OFFSET) || currentY > (height + DEFAULT_CANCEL_OFFSET)) {
                    setTextColor(if (isSelected) selectedTextColor else normalTextColor)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                setTextColor(if (isSelected) selectedTextColor else normalTextColor)
            }
        }
        return false
    }

    /**
     * 用于在被选中时修改文字的颜色
     */
    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        setTextColor(if (selected) selectedTextColor else normalTextColor)
    }

    private var onTouchCallback: ((View, MotionEvent) -> Unit)? = null
    fun setOnTouchCallback(callback: (View, MotionEvent) -> Unit) {
        this.onTouchCallback = callback
    }
}