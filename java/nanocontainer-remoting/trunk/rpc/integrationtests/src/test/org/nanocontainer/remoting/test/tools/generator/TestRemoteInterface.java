/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.test.tools.generator;

/**
 * Interface TestRemoteInterface
 * Test Interface for which the stub would be generated
 * The test is automated in the sense given the input args and expected
 * return of the function and the testcase will test for the same .
 * The method is invoked on the BCEL generated  proxy and then
 * the invocationhandler associated with the proxy  will
 * be used to complete the test cycle for the client side stub
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandrasekharan</a>
 * @version 1.0
 */
public interface TestRemoteInterface {

    String test0(String name);

    String test0_arg0 = "a for apple";
    String test0_retValue = "b for bat";


    String test1(int me);

    String test1_retValue = "c for cat";
    Integer test1_arg0 = new Integer(1);


    int test2(int you, String str);

    Integer test2_retValue = new Integer(7654);
    Integer test2_arg0 = new Integer(1);
    String test2_arg1 = new String("d for dog");

    Integer test3();

    Integer test3_retValue = new Integer(1010);

    float test4();

    Float test4_retValue = new Float(100.10);


    byte test6(byte[] lotsofdata);

    byte[] test6_arg0 = new byte[100];
    byte test6_retValue = (byte) 10;

    double test5(int[] ia, String[] sa);

    int[] test5_arg0 = {1, 2, 3};
    String[] test5_arg1 = {"a", "b", "z"};
    Double test5_retValue = new Double(1010.4545);

    void test7();

    Float test7_arg0 = new Float(10);

    StringBuffer test8(String something);

    String test8_arg0 = "";
    StringBuffer test8_retValue = new StringBuffer("I am here , Where are you?");

    StringBuffer test9(float f, double d1, Long d2, double s3, String s);

    Float test9_arg0 = new Float(10);
    Double test9_arg1 = new Double(1000);
    Long test9_arg2 = new Long(1000);
    Double test9_arg3 = new Double(1000);
    String test9_arg4 = new String("s for so long, are you a fool");
    StringBuffer test9_retValue = new StringBuffer("e for elephant");


}