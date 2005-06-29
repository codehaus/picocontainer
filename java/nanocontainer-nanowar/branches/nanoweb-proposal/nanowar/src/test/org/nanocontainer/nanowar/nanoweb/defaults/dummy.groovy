
class AnyName {

	public String assertCtor = "Ctor not called";

	AnyName() {
		assertCtor = "Ctor called";
	}

	String execute() {
		return "Yes! its me, Groovy!";
	}

}