package bibleServer;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import java.util.Date;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

class Response {
    String header;
    byte[] body;
}
class RequestHandler {

    public static Response handle(String fileRequested) {

        Response resp = new Response();
        boolean success;
        String mimeType;

        // 'fillerPrefix' is used to prevent a malformed URL exception, since my server doesn't provide
        // the scheme, authority, user info, port, or host. and it's not relevant to the method of this
        // Router class, which is to look through the path and query to pick which jRAPL request to satisfy
        // I dont intend to use url.get() any info provided by this String, just the stuff from the
        // path and query.
        String fillerPrefix = "https://localhost:1111";
        URL url = null;
        try {
            url = new URL(fillerPrefix + fileRequested);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            //@TODO some sort of error message / status for bad url? or just 404 not found?
        }

        HashMap<String,String> queryMap = queryToMap(url.getQuery());

        switch (url.getPath()) {
            case "/":
                resp.body = getPageBytes("/index.html");
                mimeType = "text/html";
                success = ! resp.body.equals(Pages.NOT_FOUND.getBytes());
                break;

            case "/api/booktext":
                String[] cmds = (
                    queryMap.get("cmd").replace("_"," ")
                ).split(";");
                ArrayList<String> results = new ArrayList<String>();
                for(int i = 0; i < cmds.length; ++i) {
                    String cmdResult = runShellCmd("./bookcmds/" + cmds[i]).strip();
                    if (cmdResult.length() > 0) results.add(cmdResult);
                }
                resp.body = String.join("\n",results).getBytes();
                mimeType = "text/plain";
                success = true;
                break;

            case "/api/wordcount":
                boolean filterStopWords = false;
                // System.out.print("queryMap: "); System.out.println(queryMap);
                if (queryMap.containsKey("filter_stopwords")) {
                    filterStopWords = queryMap.get("filter_stopwords").strip().equalsIgnoreCase("true");
                }
                String bookPath = "./bookcmds/" + queryMap.get("book");
                String[] passages = queryMap.get("passages").replace("_"," ").split(";");

                ArrayList<String> cmd = new ArrayList<String>();
                cmd.add("python3"); cmd.add("./aux-scripts/wordcount.py");
                cmd.add("-p"); cmd.add(bookPath);
                if (filterStopWords) { //TODO: make the stop word list specifiable instead of plain boolean
                    String stopWordList = "./stopwords/middle-english.txt";
                    cmd.add("-f"); cmd.add(stopWordList);
                }
                for(String passage : passages) {
                    cmd.add(passage);
                }
                String cmdResult = runShellCmd(cmd);

                resp.body = cmdResult.strip().getBytes();
                mimeType = "application/json";
                success = true;
                break;

            default:
                resp.body = getPageBytes(url.getPath());
                mimeType = getMimeType(url.getPath());
                success = ! resp.body.equals(Pages.NOT_FOUND.getBytes());
        }

        // Create header based off of collected info
        resp.header = String.join(
            "\n" ,
            "HTTP/1.1 " + ((success) ? "200 OK" : "404 NOT FOUND"), //@TODO more robust error reporting
            "Server: Server for Bible Text Requests : 1.0",
            "Date: " + new Date(),
            "Content-type: " + mimeType,
            "Content-length: " + resp.body.length,
            "Access-Control-Allow-Origin: *"
        );
        return resp;
    }

    private static byte[] getPageBytes(String fileRequested) {
        String filePath = BibleServer.WEB_ROOT + fileRequested;
        byte[] contents = null;
        FileInputStream inputStream = null;
        try {
            File fileObject = new File(filePath);
            contents = new byte[(int)fileObject.length()];
            inputStream = new FileInputStream(fileObject);
            inputStream.read(contents);
            inputStream.close();
        } catch (FileNotFoundException e) {
            return Pages.NOT_FOUND.getBytes();
        } catch (IOException e) {
            return Pages.NOT_FOUND.getBytes(); // return ("<h1>IOException!! Error message:'"+ e.getMessage() +"'</h1>").getBytes();
        }
        return contents == null ? Pages.NOT_FOUND.getBytes() : contents;
    }

    private static String readStringFromProcessStdout(Process p) {
        try {
            BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(
                    p.getInputStream()
                )
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                output.append(line);
                output.append("\n");
            }
            int exitVal = p.waitFor();
            if (exitVal == 0) {
                String outputString = output.toString();
                if (outputString.length() > 0) {
                    return outputString.strip(); //.substring(0,outputString.length()-1); // remove the last '\n'
                } else {
                    return outputString;
                }
            } else {
                // return String.format("command '%s' failed with exit code %d", cmd, exitVal);
                return String.format("command failed with exit code %d", exitVal);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return "IOException with message: " + e.getMessage();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedException with message: " + e.getMessage();
        }
    }

    public static String runShellCmd(ArrayList<String> cmdArrayList) {
        String[] cmd = new String[cmdArrayList.size()];
        for (int i = 0; i < cmdArrayList.size(); ++i) {
            cmd[i] = cmdArrayList.get(i);
        }
        System.out.println(Arrays.toString(cmd));
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            return readStringFromProcessStdout(process);
        }
        catch (IOException e) {
            e.printStackTrace();
            return (
                " -- error in runShellCmd(" +
                    Arrays.toString(cmd) +
                ") IOException with message: " + e.getMessage()
            );
        }
    }
    public static String runShellCmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            return readStringFromProcessStdout(process);
        }
        catch (IOException e) {
            e.printStackTrace();
            return (
                " -- error in runShellCmd("+cmd+"): IOException with message: "
                + e.getMessage()
            );
        }
    }

    // I got this code of the internet but I'm pretty sure it works dandily. I haven't fully confirmed and vetted its usefulness, though
    // Assumes query delimiter is '&'. Consider adding functionality for other delimiters, if need be (just have the split take a regex with the | things)
    private static HashMap<String, String> queryToMap(String query) {// throws Exception
        if (query == null) return null;
        HashMap<String, String> data = new HashMap<String, String>();
        final String[] arrParameters = query.split("&");
        for (final String tempParameterString : arrParameters) {
            final String[] arrTempParameter = tempParameterString.split("=");
            if (arrTempParameter.length >= 2) {
                final String parameterKey = arrTempParameter[0];
                final String parameterValue = arrTempParameter[1];
                data.put(parameterKey, parameterValue);
            } else {
                final String parameterKey = arrTempParameter[0];
                data.put(parameterKey, "");
            }
        }
        return data;
    }

    private static String getMimeType(String filename) {
        // thanks to 'https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types'
        // consider ordering this by frequently found types, so the correct one gets hit faster
        // some 'when youre dedicated to figure this out and when its worth it to actually do' idea is to have a data structure with
        //  all of these file extensions and their associated MIME type. It's an array of 3-item arrays, where a[0] = file extension
        //  and a[1] = MIME type and a[2] is its relative hit rate. every time you do a lookup the list gets reordered by hit rate.
        //  reordering's gotta have absolutely no runtime overhead though, otherwise having the priority order would be pointless
        //  for performance
        if (filename.endsWith(".aac"))          { return "audio/aac";                                                                 }
        else if (filename.endsWith(".abw"))     { return "application/x-abiword";                                                     }
        else if (filename.endsWith(".arc"))     { return "application/x-freearc";                                                     }
        else if (filename.endsWith(".avif"))    { return "image/avif";                                                                }
        else if (filename.endsWith(".avi"))     { return "video/x-msvideo";                                                           }
        else if (filename.endsWith(".azw"))     { return "application/vnd.amazon.ebook";                                              }
        else if (filename.endsWith(".bin"))     { return "application/octet-stream";                                                  }
        else if (filename.endsWith(".bmp"))     { return "image/bmp";                                                                 }
        else if (filename.endsWith(".bz"))      { return "application/x-bzip";                                                        }
        else if (filename.endsWith(".bz2"))     { return "application/x-bzip2";                                                       }
        else if (filename.endsWith(".cda"))     { return "application/x-cdf";                                                         }
        else if (filename.endsWith(".csh"))     { return "application/x-csh";                                                         }
        else if (filename.endsWith(".css"))     { return "text/css";                                                                  }
        else if (filename.endsWith(".csv"))     { return "text/csv";                                                                  }
        else if (filename.endsWith(".doc"))     { return "application/msword";                                                        }
        else if (filename.endsWith(".docx"))    { return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";   }
        else if (filename.endsWith(".eot"))     { return "application/vnd.ms-fontobject";                                             }
        else if (filename.endsWith(".epub"))    { return "application/epub+zip";                                                      }
        else if (filename.endsWith(".gz"))      { return "application/gzip";                                                          }
        else if (filename.endsWith(".gif"))     { return "image/gif";                                                                 }
        else if (filename.endsWith(".htm"))     { return "text/html";                                                                 }
        else if (filename.endsWith(".html"))    { return "text/html";                                                                 }
        else if (filename.endsWith(".ico"))     { return "image/vnd.microsoft.icon";                                                  }
        else if (filename.endsWith(".ics"))     { return "text/calendar";                                                             }
        else if (filename.endsWith(".jar"))     { return "application/java-archive";                                                  }
        else if (filename.endsWith(".jpeg"))    { return "image/jpeg";                                                                }
        else if (filename.endsWith(".jpg"))     { return "image/jpeg";                                                                }
        else if (filename.endsWith(".js"))      { return "text/javascript";                                                           } // (Specifications: HTML and its reasoning, and IETF)
        else if (filename.endsWith(".json"))    { return "application/json";                                                          }
        else if (filename.endsWith(".jsonld"))  { return "application/ld+json";                                                       }
        else if (filename.endsWith(".mid"))     { return "audio/midi audio/x-midi";                                                   }
        else if (filename.endsWith(".midi"))    { return "audio/midi audio/x-midi";                                                   }
        else if (filename.endsWith(".mjs"))     { return "text/javascript";                                                           }
        else if (filename.endsWith(".mp3"))     { return "audio/mpeg";                                                                }
        else if (filename.endsWith(".mp4"))     { return "video/mp4";                                                                 }
        else if (filename.endsWith(".mpeg"))    { return "video/mpeg";                                                                }
        else if (filename.endsWith(".mpkg"))    { return "application/vnd.apple.installer+xml";                                       }
        else if (filename.endsWith(".odp"))     { return "application/vnd.oasis.opendocument.presentation";                           }
        else if (filename.endsWith(".ods"))     { return "application/vnd.oasis.opendocument.spreadsheet";                            }
        else if (filename.endsWith(".odt"))     { return "application/vnd.oasis.opendocument.text";                                   }
        else if (filename.endsWith(".oga"))     { return "audio/ogg";                                                                 }
        else if (filename.endsWith(".ogv"))     { return "video/ogg";                                                                 }
        else if (filename.endsWith(".ogx"))     { return "application/ogg";                                                           }
        else if (filename.endsWith(".opus"))    { return "audio/opus";                                                                }
        else if (filename.endsWith(".otf"))     { return "font/otf";                                                                  }
        else if (filename.endsWith(".png"))     { return "image/png";                                                                 }
        else if (filename.endsWith(".pdf"))     { return "application/pdf";                                                           }
        else if (filename.endsWith(".php"))     { return "application/x-httpd-php";                                                   }
        else if (filename.endsWith(".ppt"))     { return "application/vnd.ms-powerpoint";                                             }
        else if (filename.endsWith(".pptx"))    { return "application/vnd.openxmlformats-officedocument.presentationml.presentation"; }
        else if (filename.endsWith(".rar"))     { return "application/vnd.rar";                                                       }
        else if (filename.endsWith(".rtf"))     { return "application/rtf";                                                           }
        else if (filename.endsWith(".sh"))      { return "application/x-sh";                                                          }
        else if (filename.endsWith(".svg"))     { return "image/svg+xml";                                                             }
        else if (filename.endsWith(".swf"))     { return "application/x-shockwave-flash";                                             }
        else if (filename.endsWith(".tar"))     { return "application/x-tar";                                                         }
        else if (filename.endsWith(".ts"))      { return "video/mp2t";                                                                }
        else if (filename.endsWith(".ttf"))     { return "font/ttf";                                                                  }
        else if (filename.endsWith(".txt"))     { return "text/plain";                                                                }
        else if (filename.endsWith(".vsd"))     { return "application/vnd.visio";                                                     }
        else if (filename.endsWith(".wav"))     { return "audio/wav";                                                                 }
        else if (filename.endsWith(".weba"))    { return "audio/webm";                                                                }
        else if (filename.endsWith(".webm"))    { return "video/webm";                                                                }
        else if (filename.endsWith(".webp"))    { return "image/webp";                                                                }
        else if (filename.endsWith(".woff"))    { return "font/woff";                                                                 }
        else if (filename.endsWith(".woff2"))   { return "font/woff2";                                                                }
        else if (filename.endsWith(".xhtml"))   { return "application/xhtml+xml";                                                     }
        else if (filename.endsWith(".xls"))     { return "application/vnd.ms-excel";                                                  }
        else if (filename.endsWith(".xlsx"))    { return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";         }
        else if (filename.endsWith(".xml"))     { return "application/xml";                                                           }
        else if (filename.endsWith(".xul"))     { return "application/vnd.mozilla.xul+xml";                                           }
        else if (filename.endsWith(".zip"))     { return "application/zip";                                                           }
        else if (filename.endsWith(".3gp"))     { return "video/3gpp; audio/3gpp if it doesn't contain video";                        }
        else if (filename.endsWith(".3g2"))     { return "video/3gpp2; audio/3gpp2 if it doesn't contain video";                      }
        else if (filename.endsWith(".7z"))      { return "application/x-7z-compressed";                                               }
        else if (filename.endsWith(".tif"))     { return "image/tiff";                                                                }
        else if (filename.endsWith(".tiff"))    { return "image/tiff";                                                                }
        else                                    { return "text/plain";                                                                }
    }
}
