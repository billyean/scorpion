package com.haiboyan.filesearch

import java.io.File
import java.nio.file.Files.list
import java.nio.file.Paths.get

import scala.annotation.tailrec

class Matcher(filter: String, root: String, checkSubFolder: Boolean = false, contentFilter: Option[String] = None) {
  val rootIOObject = FileConverter.convertToIOObject(new File(root))

  def execute() = {
    @tailrec
    def recursiveMatch(files: List[IOObject], currentList: List[FileObject]): List[FileObject] =
      files match {
        case List() => currentList
        case iOObject :: rest =>
          iOObject match {
            case file : FileObject if FilterChecker(filter) matches file  =>
              recursiveMatch(rest, file :: currentList)
            case directory : DirectoryObject =>
              recursiveMatch(rest ::: directory.children(), currentList)
            case _ => recursiveMatch(rest, currentList)
          }
      }

    val matchedFiles = rootIOObject match  {
      case file : FileObject if FilterChecker(filter).matches(file) => List(file)
      case directory: DirectoryObject =>
        if (checkSubFolder)
          recursiveMatch(directory.children(), List())
        else
          FilterChecker(filter) findMatchedFiles directory.children()
      case _ => List()
    }

    matchedFiles map(file => file.name)
  }
}
