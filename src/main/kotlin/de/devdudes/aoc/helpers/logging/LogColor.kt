package de.devdudes.aoc.helpers.logging

import de.devdudes.aoc.helpers.logging.LogColor.Companion.toRGB
import de.devdudes.aoc.helpers.logging.LogColor.GreyScale

/**
 * for more colors and options see:
 * https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797#256-colors
 */
sealed class LogColor(val rawColor: String) {

    data object Black : LogColor("0")
    data object White : LogColor("15")
    data object Red : LogColor("1")
    data object Green : LogColor("2")
    data object Yellow : LogColor("3")
    data object Blue : LogColor("4")
    data object Magenta : LogColor("5")
    data object Cyan : LogColor("6")

    data class Raw(val id: Int) : LogColor(id.toString())
    data class RGB(val r: Int, val g: Int, val b: Int) : LogColor("$r;$g;$b")

    sealed class GreyScale(rawColor: String) : LogColor(rawColor) {
        data object Grey01 : LogColor("255")
        data object Grey02 : LogColor("254")
        data object Grey03 : LogColor("253")
        data object Grey04 : LogColor("252")
        data object Grey05 : LogColor("251")
        data object Grey06 : LogColor("250")
        data object Grey07 : LogColor("249")
        data object Grey08 : LogColor("248")
        data object Grey09 : LogColor("247")
        data object Grey10 : LogColor("246")
        data object Grey11 : LogColor("245")
        data object Grey12 : LogColor("244")
        data object Grey13 : LogColor("243")
        data object Grey14 : LogColor("242")
        data object Grey15 : LogColor("241")
        data object Grey16 : LogColor("240")
        data object Grey17 : LogColor("239")
        data object Grey18 : LogColor("238")
        data object Grey19 : LogColor("237")
        data object Grey20 : LogColor("236")
        data object Grey21 : LogColor("235")
        data object Grey22 : LogColor("234")
        data object Grey23 : LogColor("233")
        data object Grey24 : LogColor("232")
    }

    companion object {
        val AllColors: List<Raw> = List(231) { Raw(it) }

        fun LogColor.toRGB(): RGB =
            when (this) {
                Black -> RGB(0, 0, 0)
                White -> RGB(255, 255, 255)
                Red -> RGB(255, 0, 0)
                Green -> RGB(0, 255, 0)
                Yellow -> RGB(255, 255, 0)
                Blue -> RGB(0, 0, 255)
                Magenta -> RGB(255, 0, 255)
                Cyan -> RGB(0, 255, 255)
                else -> error("RGB not defined $this")
            }
    }
}

fun String.colored(color: LogColor): String =
    if (color is LogColor.RGB) {
        coloredRGB(color.rawColor)
    } else {
        colored(color.rawColor)
    }

fun String.colored(color: String): String = "\u001B[38;5;${color}m" + this + "\u001B[0m"
fun String.coloredRGB(color: String): String = "\u001B[38;2;${color}m" + this + "\u001B[0m"

fun String.background(color: LogColor): String = background(color.rawColor)
fun String.background(color: String): String = "\u001B[48;5;${color}m" + this + "\u001B[0m"

enum class GradientDirection { LIGHT_TO_DARK, DARK_TO_LIGHT }
sealed class GradientColors(val colors: List<LogColor>) {
    data object GreyScale : GradientColors(greys())
    data object GreyScaleDark : GradientColors(greys().takeLast(greys().size / 2))
    data object GreyScaleLight : GradientColors(greys().take(greys().size / 2))
    data class CustomGradient(val start: LogColor.RGB, val end: LogColor.RGB, val steps: Int) : GradientColors(createColorGradient(start, end, steps))

    companion object {
        fun from(start: LogColor, end: LogColor, steps: Int): GradientColors = CustomGradient(start.toRGB(), end.toRGB(), steps)
    }
}

fun gradient(
    value: Int,
    range: Int,
    gradient: GradientColors = GradientColors.GreyScale,
    mode: GradientDirection = GradientDirection.DARK_TO_LIGHT
): LogColor {
    val colors = if (mode == GradientDirection.DARK_TO_LIGHT) gradient.colors else gradient.colors.reversed()
    val gradientColorCount = colors.size
    val index = ((gradientColorCount.toFloat() / (range + 1).toFloat()) * value).toInt()
    val validIndex = index.coerceIn(0, colors.lastIndex)
    return colors[validIndex]
}

fun greyscale(value: Int, range: Int = greys().size, mode: GradientDirection = GradientDirection.DARK_TO_LIGHT): LogColor =
    gradient(
        value = value,
        range = range,
        gradient = GradientColors.GreyScale,
        mode = mode,
    )

private fun createColorGradient(start: LogColor.RGB, end: LogColor.RGB, steps: Int): List<LogColor> {
    fun interpolate(from: Int, to: Int, percent: Float): Int {
        val range = to - from
        val valueInRange = range * percent
        return from + valueInRange.toInt()
    }

    val stepFactor = 1f / (steps - 1)
    return List(steps) { index ->
        LogColor.RGB(
            r = interpolate(start.r, end.r, index * stepFactor),
            g = interpolate(start.g, end.g, index * stepFactor),
            b = interpolate(start.b, end.b, index * stepFactor),
        )
    }
}

private fun greys(): List<LogColor> = listOf(
    GreyScale.Grey01,
    GreyScale.Grey02,
    GreyScale.Grey03,
    GreyScale.Grey04,
    GreyScale.Grey05,
    GreyScale.Grey06,
    GreyScale.Grey07,
    GreyScale.Grey08,
    GreyScale.Grey09,
    GreyScale.Grey10,
    GreyScale.Grey11,
    GreyScale.Grey12,
    GreyScale.Grey13,
    GreyScale.Grey14,
    GreyScale.Grey15,
    GreyScale.Grey16,
    GreyScale.Grey17,
    GreyScale.Grey18,
    GreyScale.Grey19,
    GreyScale.Grey20,
    GreyScale.Grey21,
    GreyScale.Grey22,
    GreyScale.Grey23,
    GreyScale.Grey24,
)
