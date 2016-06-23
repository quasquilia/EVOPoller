/*
 * Copyright 2011 http://pvoutput.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.guarascio.evopoller.pvoutput;

public final class Constant 
{
	private Constant()
	{
		
	}
	public static final int ELAPSED_LIMIT = 5;
	public static final int HTTP_CONNECT_TIMEOUT = 15000;
	public static final int HTTP_SO_TIMEOUT = 15000;
	public static final double DEFAULT_TEMPERATURE = -1000;
	public static final double DEFAULT_VOLTAGE = -1;
	public static final int MAX_BATCH_SIZE = 30;
}
