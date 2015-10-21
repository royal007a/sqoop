/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.test.utils;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.log4j.Logger;
import org.apache.sqoop.connector.hdfs.configuration.ToFormat;
import org.apache.sqoop.model.MConfigList;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.test.hadoop.HadoopRunner;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;

/**
 * Handy utilities to work with HDFS
 */
public class HdfsUtils {

  private static final Logger LOG = Logger.getLogger(HdfsUtils.class);

  private static final char PATH_SEPARATOR = '/';

  public static final PathFilter filterHiddenFiles = new PathFilter() {
    @Override
    public boolean accept(Path path) {
      String fileName = path.getName();
      return !fileName.startsWith("_") && !fileName.startsWith(".");
    }
  };

  /**
   * Get list of mapreduce output files from given directory.
   *
   * @param directory Directory to be searched for files generated by MR
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static Path [] getOutputMapreduceFiles(FileSystem fs, String directory) throws IOException {
    LinkedList<Path> files = new LinkedList<Path>();
    for (FileStatus fileStatus : fs.listStatus(new Path(directory), filterHiddenFiles)) {
      LOG.debug("Found mapreduce output file: " + fileStatus.getPath() + " with size " + fileStatus.getLen());
      if (fileStatus.isFile()) {
        files.add(fileStatus.getPath());
      }
    }
    return files.toArray(new Path[files.size()]);
  }

  /**
   * Create HDFS file with given content.
   *
   * @param fs filesystem object
   * @param path path to file be created
   * @param lines Individual lines that should be written into the file
   * @throws IOException
   */
  public static void createFile(FileSystem fs, String path, String ...lines) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(fs.create(new Path(path), true), Charset.forName("UTF-8")))) {
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    }
  }

  /**
   * Join several path fragments together.
   * @param paths
   */
  public static String joinPathFragments(String ...paths){
    StringBuilder builder = new StringBuilder();
    for (String path : paths) {
      builder.append(path);
      if (path.charAt(path.length() - 1) != PATH_SEPARATOR) {
        builder.append(PATH_SEPARATOR);
      }
    }
    return builder.toString();
  }

  private HdfsUtils() {
    // Instantiation is not allowed
  }
}
