<?php

class ContainerHierarchiesTests extends UnitTestCase {
   
  public function testResolvesFromParentContainer(){
      
      $parent = new DefaultPicoContainer();
      $parent->regComponentImpl('SimpleTouchable');
      
      $child = new DefaultPicoContainer(null, $parent);
      $child->regComponentImpl('DependsOnTouchable');
      
      $this->assertNotNull($child->getComponentInstance('DependsOnTouchable'));
   }
   
   
   public function testDoesntResolveFromParentContainer(){
      
      $parent = new DefaultPicoContainer();
      $parent->regComponentImpl('DependsOnTouchable');
      
      $child = new DefaultPicoContainer(null, $parent);
      $child->regComponentImpl('SimpleTouchable');
      
      try {
         $this->assertNotNull($parent->getComponentInstance('DependsOnTouchable'));
         $this->fail();
      } catch (UnsatisfiableDependenciesException $e){
         $this->pass();
      }  
   }
   
   public function testDoesntResolveFromParentContainerWhenHasDependencyInside(){
      
      $parent = new DefaultPicoContainer();
      $parent->regComponentImpl('SimpleTouchable');
      
      $child = new DefaultPicoContainer(null, $parent);
      $child->regComponentImpl('AlternativeTouchable');
      $child->regComponentImpl('DependsOnTouchable');
      
      $dependsOnTouchable = $child->getComponentInstance('DependsOnTouchable');
      $this->assertNotNull($dependsOnTouchable);
      $this->assertIsA($dependsOnTouchable->getTouchable(), 'AlternativeTouchable');      
   }

   public function testResolvesInParentWhenNoComponentInTheActualContainer(){
      $parent = new DefaultPicoContainer();
      $parent->regComponentImpl('SimpleTouchable');
      
      $child = new DefaultPicoContainer(null, $parent);
      $this->assertIsA($child->getComponentInstance('SimpleTouchable'), 'SimpleTouchable');
   }
   
   public function testResolvesInParentWhenNoComponentInTheActualContainerAndLookUpByType(){
      $parent = new DefaultPicoContainer();
      $parent->regComponentImpl('SimpleTouchable');
      
      $child = new DefaultPicoContainer(null, $parent);
      $this->assertIsA($child->getComponentInstanceOfType('Touchable'), 'SimpleTouchable');
   }
   
   public function testResolvesInParentWhenNoComponentInTheActualContainerAndLookUpByTypeAndRegusteredByAnyKey(){
      $parent = new DefaultPicoContainer();
      $parent->regComponentImpl('SimpleTouchableAnyKey','SimpleTouchable');
      
      $child = new DefaultPicoContainer(null, $parent);
      $this->assertIsA($child->getComponentInstanceOfType('Touchable'), 'SimpleTouchable');
   }
   
   public function testResolvesInGrandParent(){
      $grandparent = new DefaultPicoContainer();
      $grandparent->regComponentImpl('SimpleTouchableAnyKey','SimpleTouchable');
      
      $parent = new DefaultPicoContainer(null, $grandparent);
      
      $child = new DefaultPicoContainer(null, $parent);
      $this->assertIsA($child->getComponentInstanceOfType('Touchable'), 'SimpleTouchable');
   }
}
?>