public interface MessageReceiver {
	void gotMessage(String line);
	void gotCoordinates(String line);
	void setDrawButtonVisible();
}