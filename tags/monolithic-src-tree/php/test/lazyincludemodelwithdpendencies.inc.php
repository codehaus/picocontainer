<?php

class LazyIncludeModelWithDpendencies
{
	private $simpleTouchable;
	
	public function __construct(LazyIncludeModelDependend $simpleTouchable)
	{
		$this->simpleTouchable = $simpleTouchable;
	}
	
	public function touch() 
	{
		$this->simpleTouchable->touch();
	}
	
}

?>