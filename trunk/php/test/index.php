<?php

/*
 * You nead to install SimpleTest to run tests
 * @link http://www.lastcraft.com/simple_test.php
 */
 
require_once('../../simpletest/unit_tester.php');
require_once('../../simpletest/reporter.php');

require_once('../src/pico.inc.php');
require_once('model.classes.inc.php');

$test = &new GroupTest('All Pico tests');
$test->addTestFile('ComponentKeyTests.php');
$test->addTestFile('ComponentAdapterTests.php');
$test->addTestFile('DefaultPicoContainerTests.php');
$test->addTestFile('ExceptionsTests.php');

if (TextReporter::inCli()) {
        exit ($test->run(new TextReporter()) ? 0 : 1);
}
$test->run(new HtmlReporter()); 

?>