<?

interface Touchable 
{
    public function touch();
}


class SimpleTouchable implements Touchable
{
    public $wasTouched = false;   
    
    public function touch() 
    {
        $this->wasTouched = true;
    }
}

class AlternativeTouchable implements Touchable
{
    public $wasTouched = false;
    
    public function touch() 
    {
        $this->wasTouched = true;
    }
}

class DependsOnTouchable
{
    public $touchable;

    public function __construct(Touchable $touchable) 
    {
        //Assert.assertNotNull("Touchable cannot be passed in as null", touchable);
        $touchable->touch();
        $this->touchable = $touchable;
    }

    public function getTouchable() {
        return $this->touchable;
    }
}

class DependsOnSimpleTouchable
{
    public $touchable;

    public function __construct(SimpleTouchable $touchable) 
    {        
        $touchable->touch();
        $this->touchable = $touchable;
    }

    public function getTouchable() {
        return $this->touchable;
    }
}

class DerivedTouchable extends SimpleTouchable 
{       
}    

class DecoratedTouchable implements Touchable
{       
	 private $delegate;
	 
	 public function __construct(Touchable $delegate)
	 {
	 	$this->delegate = $delegate;
	 }
	 
	 public function touch() {
        $this->delegate->touch();
    }
}


class Boy
{    
    public function kiss($girl)
    {
        return 'I was kissed by a '.get_class($girl);        
    }
}


class BoyWithConstantParam extends Boy
{
    private $_constparam;
    
    public function __construct($param)
    {
        $this->_constparam = $param;
    }
    
    public function getParam()
    {
        return $this->_constparam;
    }
}

class Girl
{    
    private $_boy;
    
    public function __construct(Boy $boy)
    {
        $this->_boy = $boy;
    }
    
    public function kissSomeone()
    {
        return $this->_boy->kiss($this);
    }
}

class GirlWithoutBoyType
{
    private $sthLikeBoy;
    
    public function __construct($sthLikeBoy)
    {
        $this->sthLikeBoy = $sthLikeBoy;
    }
    
    public function getBoy()
    {
        return $this->sthLikeBoy;
    }
}

class C1
{
    public function __construct(C2 $c2)
    {
    }
}

class C2
{
    public function __construct(C1 $c1)
    {
    }
}

class EmptyBean 
{	
}

class PersonBean 
{
    private $name;
    public function setName($name) 
    {
        $this->name = $name;
    }
    public function getName() 
    {
        return $this->name;
    }
}

class PurseBean 
{	
    private $owner;
    
    public function getOwner() 
    {
        return $this->owner;
    }
    
    public function setOwner(PersonBean $owner) 
    {
        $this->owner = $owner;
    }
}

?>