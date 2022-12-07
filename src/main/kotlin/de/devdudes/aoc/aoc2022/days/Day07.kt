package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.aoc2022.days.FileStructure.Directory
import de.devdudes.aoc.aoc2022.days.FileStructure.File
import de.devdudes.aoc.aoc2022.days.TerminalOutput.Command.*
import de.devdudes.aoc.aoc2022.days.TerminalOutput.Data.DirectoryListing
import de.devdudes.aoc.aoc2022.days.TerminalOutput.Data.FileListing
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day07 : Day(
    description = 7 - "No Space Left On Device",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Directories with a total size of at most 100.000",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 95437L,
            solutionResult = 1555642L,
            solution = { input ->
                parseFileSystem(input)
                    .getFoldersWithMaxSize(100_000)
                    .sumOf { it.size }
            }
        )

        puzzle(
            description = 2 - "Free up disk space",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 24933642L,
            solutionResult = 5974547L,
            solution = { input ->
                val fileSystem = parseFileSystem(input)
                fileSystem
                    .getFoldersWithMinSize(30_000_000 - (70_000_000 - fileSystem.size))
                    .minOf { it.size }
            }
        )
    }
)

private fun parseFileSystem(input: List<String>): Directory {

    fun buildFileSystemDirectory(outputs: List<TerminalOutput>): Pair<List<FileStructure>, List<TerminalOutput>> {

        val mutableOutputs = outputs.toMutableList()
        val result = mutableListOf<FileStructure>()

        while (mutableOutputs.isNotEmpty()) {
            when (val output = mutableOutputs.removeFirst()) {
                GoToParentDir -> break

                is GoToSubDir -> {
                    val (fileStructure, newOutput) = buildFileSystemDirectory(outputs = mutableOutputs)
                    mutableOutputs.clear()
                    mutableOutputs.addAll(newOutput)

                    val directory = result.first { it.name == output.name } as Directory
                    val index = result.indexOf(directory)
                    result[index] = directory.copy(files = directory.files + fileStructure)
                }

                ListFiles -> Unit // nothing to do

                is DirectoryListing ->
                    result.add(
                        Directory(name = output.name, files = emptyList())
                    )

                is FileListing ->
                    result.add(
                        File(name = output.name, size = output.size)
                    )
            }
        }

        return result to mutableOutputs
    }

    val terminalOutput = parseTerminalOutput(
        input.drop(1), // first line navigates to root
    )

    return Directory(
        name = "/",
        files = buildFileSystemDirectory(terminalOutput).first,
    )
}

private sealed class FileStructure {

    abstract val name: String
    abstract val size: Long

    data class Directory(
        override val name: String,
        val files: List<FileStructure>,
    ) : FileStructure() {
        override val size: Long
            get() = files.sumOf { it.size }

        fun findFolders(predicate: (Directory) -> Boolean): List<Directory> =
            files.flatMap { fileStructure ->
                when (fileStructure) {
                    is Directory -> {
                        val directories = fileStructure.findFolders(predicate)
                        if (predicate(fileStructure)) directories + fileStructure
                        else directories
                    }

                    is File -> emptyList()
                }
            }

        fun getFoldersWithMaxSize(maxSize: Long): List<Directory> = findFolders { it.size <= maxSize }
        fun getFoldersWithMinSize(minSize: Long): List<Directory> = findFolders { it.size > minSize }
    }

    data class File(
        override val name: String,
        override val size: Long,
    ) : FileStructure()
}

private fun parseTerminalOutput(input: List<String>): List<TerminalOutput> {
    fun parseCommand(line: String): TerminalOutput {
        val commandInput = line.drop(2)
        return when {
            commandInput.startsWith("cd") -> {
                when (val operation = commandInput.split(" ").last()) {
                    ".." -> GoToParentDir
                    else -> GoToSubDir(name = operation)
                }
            }

            commandInput.startsWith("ls") -> ListFiles
            else -> throw UnsupportedOperationException("unknown command: $line")
        }
    }

    fun parseDir(line: String): TerminalOutput = DirectoryListing(name = line.split(" ").last())

    fun parseFile(line: String): TerminalOutput {
        val (size, name) = line.split(" ")
        return FileListing(
            name = name,
            size = size.toLong(),
        )
    }

    return input.map { line ->
        when {
            line.startsWith("$") -> parseCommand(line)
            line.startsWith("dir") -> parseDir(line)
            else -> parseFile(line)
        }
    }
}

private sealed class TerminalOutput {
    sealed class Command : TerminalOutput() {
        data class GoToSubDir(val name: String) : Command()
        object GoToParentDir : Command()
        object ListFiles : Command()
    }

    sealed class Data : TerminalOutput() {

        abstract val name: String

        data class DirectoryListing(override val name: String) : Data()

        data class FileListing(
            override val name: String,
            val size: Long,
        ) : Data()
    }
}
