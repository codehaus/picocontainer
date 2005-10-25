<?php

error_reporting(E_ALL);

/**
 * You nead to install SimpleTest to run tests
 * {@link http://www.lastcraft.com/simple_test.php}
 */
 
define('SIMPLETEST_PATH', 'D:/phplibs/simpletest_1.0.1alpha2');
define('PICOCONTAINER_TEST_PATH', dirname(__FILE__));
define('PICOCONTAINER_PATH', PICOCONTAINER_TEST_PATH . '/../src');

require_once(SIMPLETEST_PATH . '/unit_tester.php');
require_once(SIMPLETEST_PATH . '/reporter.php');
//require_once SIMPLETEST_PATH . '/ui/colortext_reporter.php';

require_once(PICOCONTAINER_PATH . '/pico.inc.php');
require_once('model.classes.inc.php');

$test = new GroupTest('All Pico tests');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/ComponentKeyTests.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/ComponentAdapterTests.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/DefaultPicoContainerTests.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/ExceptionsTests.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/SetterInjectionComponentAdapterTestCase.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/LazyIncludingCATests.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/SitePointTests.php');
$test->addTestFile(PICOCONTAINER_TEST_PATH . '/ParameterTests.php');

if (TextReporter::inCli()) {
        exit ($test->run(new ColorTextReporter()) ? 0 : 1);
}
$test->run(new HtmlReporter()); 

?>