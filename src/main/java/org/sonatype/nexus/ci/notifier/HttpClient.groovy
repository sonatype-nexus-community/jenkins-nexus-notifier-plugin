package org.sonatype.nexus.ci.notifier

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

class HttpClient
{
  def putCard(url, requestBody, requestHeaders) {
    def http = new HTTPBuilder(url)
    return http.request(Method.PUT, JSON) {
      req ->
        body = requestBody
        headers = requestHeaders
    }
  }
}
