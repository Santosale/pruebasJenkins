package forms;

import org.hibernate.validator.constraints.URL;

public class UserForm extends ActorForm {
	
	private String avatar;
	
	@URL
	public String getAvatar() {
		return this.avatar;
	}
	
	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

}