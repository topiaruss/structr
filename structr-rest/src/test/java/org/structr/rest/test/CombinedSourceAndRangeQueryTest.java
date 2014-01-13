/**
 * Copyright (C) 2010-2013 Axel Morgner, structr <structr@structr.org>
 *
 * This file is part of structr <http://structr.org>.
 *
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.rest.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import org.structr.rest.common.StructrRestTest;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Axel Morgner
 */
public class CombinedSourceAndRangeQueryTest extends StructrRestTest {

	/**
	 * Test range search in combination with an entity property
	 */
	public void testCombined1() {

		String location = RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'name' : 'test' } ").expect().statusCode(201).when().post("/test_sevens").getHeader("Location");
		String testSevenId = getUuidFromLocation(location);
		
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-03T12:34:56+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-05T10:43:40+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-07T10:43:40+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");

		// test range query
		RestAssured.given()
			.contentType("application/json; charset=UTF-8")
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))
		.expect()
			.statusCode(200)
			.body("result_count", equalTo(2))
		.when()
			.get("/test_elevens?testSevenId=" + testSevenId + "&dateProperty=[2013-04-01T00:00:00+0200 TO 2013-04-06T23:59:59+0200]");
	
	}	

	public void testCombined2() {

		String location = RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'name' : 'test' } ").expect().statusCode(201).when().post("/test_sevens").getHeader("Location");
		String testSevenId = getUuidFromLocation(location);
		
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-03T12:34:56+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-05T10:43:40+0200' } ").expect().statusCode(201).when().post("/test_elevens");
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-07T10:43:40+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");

		// test range query
		RestAssured.given()
			.contentType("application/json; charset=UTF-8")
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))
		.expect()
			.statusCode(200)
			.body("result_count", equalTo(1))
			.body("result[0].dateProperty", equalTo("2013-04-03T12:34:56+0200"))
		.when()
			.get("/test_elevens?testSevenId=" + testSevenId + "&dateProperty=[2013-04-01T00:00:00+0200 TO 2013-04-06T23:59:59+0200]");
	
	}	

	public void testCombined3() {

		String location = RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'name' : 'test' } ").expect().statusCode(201).when().post("/test_sevens").getHeader("Location");
		String testSevenId = getUuidFromLocation(location);
		
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-03T12:34:56+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-05T10:43:40+0200' } ").expect().statusCode(201).when().post("/test_elevens");
		RestAssured.given().contentType("application/json; charset=UTF-8").body(" { 'dateProperty' : '2013-04-07T10:43:40+0200', 'testSevenId' : '" + testSevenId  + "' } ").expect().statusCode(201).when().post("/test_elevens");

		// test range query
		RestAssured.given()
			.contentType("application/json; charset=UTF-8")
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(201))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(400))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(422))
			.filter(ResponseLoggingFilter.logResponseIfStatusCodeIs(500))
		.expect()
			.statusCode(200)
			.body("result_count", equalTo(1))
			.body("result[0].dateProperty", equalTo("2013-04-05T10:43:40+0200"))
		.when()
			.get("/test_elevens?testSevenId=&dateProperty=[2013-04-01T00:00:00+0200 TO 2013-04-06T23:59:59+0200]");
	
	}	
}
