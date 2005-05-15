<?php

class LazyIncludeModel
{
	private $simpleTouchable;
	
	public function __construct(SimpleTouchable $simpleTouchable)
	{
		$this->simpleTouchable = $simpleTouchable;
	}
	
	public function touch() 
	{
		$this->simpleTouchable->touch();
	}
	
}

?>