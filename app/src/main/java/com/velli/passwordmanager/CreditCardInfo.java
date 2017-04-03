/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) [2017] [velli20]
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.velli.passwordmanager;

public class CreditCardInfo {
	private String mCardType;
	private String mCardNumber;
	private String mCardExpirationDate;
	private String mCardSecurityNumber;
	private String mCardGroup;

	public CreditCardInfo(){
		
	}

	public void setCardType(String name){
		mCardType = name;
	}
	
	public void setCardNumber(String number){
		mCardNumber = number;
	}
	
	public void setCardExpirationDate(String date){
		mCardExpirationDate = date;
	}
	
	public void setCardCSV(String csv){
		mCardSecurityNumber = csv;
	}
	
	public void setGroup(String group){
		mCardGroup = group;
	}

	public String getCardType(){
		return mCardType;
	}
	
	public String getCardNumber(){
		return mCardNumber;
	}
	
	public String getCardExpirationDate(){
		return mCardExpirationDate;
	}
	
	public String getCardCSV(){
		return mCardSecurityNumber;
	}
	
	public String getGroup(){
		return mCardGroup;
	}


}
