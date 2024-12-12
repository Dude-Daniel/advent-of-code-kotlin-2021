package de.devdudes.aoc.helpers.logging

import de.devdudes.aoc.helpers.logging.LogColor.GreyScale

/**
 * for more colors and options see:
 * https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797#256-colors
 */
sealed class LogColor(val rawColor: String) {

    data object Black : LogColor("0")
    data object Red : LogColor("1")
    data object Green : LogColor("2")
    data object Yellow : LogColor("3")
    data object Blue : LogColor("4")
    data object Magenta : LogColor("5")
    data object Cyan : LogColor("6")

    data class Raw(val id: Int) : LogColor(id.toString())

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
    }
}

fun String.colored(color: LogColor): String = colored(color.rawColor)
fun String.colored(color: String): String = "\u001B[38;5;${color}m" + this + "\u001B[0m"

fun String.background(color: LogColor): String = background(color.rawColor)
fun String.background(color: String): String = "\u001B[48;5;${color}m" + this + "\u001B[0m"

fun greyscale(value: Int, range: Int = greys().size): LogColor {
    val index = ((greys().size.toFloat() / range.toFloat()) * value).toInt()
    return greys()[index]
}

private fun greys() = listOf(
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
