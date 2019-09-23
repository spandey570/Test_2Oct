package com.hyke.portal.utils;


import java.util.Random;

import org.apache.log4j.Logger;


public class RandomUtil {
    public final static Logger log = Logger.getLogger(RandomUtil.class);
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHAR = "!@#$%^&*";
    private static final String INT_CHAR = "0123456789";
    Random rnd;

    public RandomUtil() {
        rnd = new Random();
    }

    /**
     * This method will return Random numbers depending on the provided size of the random number.
     *
     * @param size Size of the character length required
     * @return random number of size provided
     */
    public String getRandomNumber(int size) {
        log.info("Creating object of Random Class");
        StringBuilder sb = new StringBuilder(size);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i++)
            sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();

    }

    /**
     * This method will return Random string depending on the provided size of the random string.
     *
     * @param size Size of the character length required
     * @return random string of size provided
     */
    public String getRandomString(int size) {

        log.info("Creating object of Random Class");
        StringBuilder sb = new StringBuilder(size);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i++)
            sb.append(LOWER_CASE.charAt(rnd.nextInt(LOWER_CASE.length())));
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();

    }

    /**
     * This method will return String in caps depending on the provided size of the random number.
     *
     * @param size Size of the character length required
     * @return random String all in capital letters of size provided
     */
    public String getRandomStringAllCaps(int size) {

        log.info("Creating object of Random Class");
        StringBuilder sb = new StringBuilder(size);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i++)
            sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();

    }

    /**
     * This method will return Random alpha numeric string depending on the provided size of the random number.
     *
     * @param size Size of the character length required
     * @return Alpha numerical characters of the provided size
     */
    public String getRandomAlphaNumeric(int size) {
        log.info("Creating object of Random Class");
        StringBuilder sb = new StringBuilder(size);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i = i + 1) {
            if (i % 2 == 0)
                sb.append(LOWER_CASE.charAt(rnd.nextInt(LOWER_CASE.length())));
            else
                sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
        }
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();
    }

    /**
     * This method will return AlphaNumeric Values where characters will be in Capital
     *
     * @param size
     * @return Alphanumeric word
     */
    public String getRandomAlphaNumericWithCaps(int size) {
        log.info("Creating object of Random Class");
        StringBuilder sb = new StringBuilder(size);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i = i + 1) {
            if (i % 2 == 0)
                sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
            else
                sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
        }
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();
    }

    /**
     * This method will return AlphaNumeric Values where characters with the defined size of the String and numbers
     *
     * @param sizeString
     * @param sizeNumber
     * @return Alpha numeric words with defined size
     */
    public String getRandomAlphaNumeric(int sizeString, int sizeNumber) {
        log.info("Creating object of Random Class");
        int size = sizeNumber + sizeString;
        int stringCounter = 0;
        int numberCounter = 0;
        StringBuilder sb = new StringBuilder(sizeString + sizeNumber);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i = i + 1) {
            if (i % 2 == 0) {
                if (stringCounter != sizeString) {
                    sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
                    stringCounter++;
                } else {
                    sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
                    numberCounter++;
                }
            } else {
                if (numberCounter != sizeNumber) {
                    sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
                    numberCounter++;
                } else {
                    sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
                    stringCounter++;
                }
            }
        }
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();

    }

    /**
     * This method will return AlphaNumeric  with special characters
     *
     * @param size
     * @return Random Alpha Numeric With Special Character
     */
    public String getRandomAlphaNumericWithSpecialCharacter(int size) {
        log.info("Creating object of Random Class");
        StringBuilder sb = new StringBuilder(size);
        log.info("Creating a string builder with size of Required new string");
        for (int i = 0; i < size; i = i + 1) {
            char choice = INT_CHAR.charAt(rnd.nextInt(3));
            System.out.println(choice);
            switch (choice) {
                case '0':
                    sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
                    break;
                case '1':
                    sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
                    break;
                case '2':
                    sb.append(SPECIAL_CHAR.charAt(rnd.nextInt(SPECIAL_CHAR.length())));
                    break;
            }
        }
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();
    }

    /**
     * This method will return AlphaNumeric  with special characters with defined size of string, numbers and special characters
     *
     * @param sizeString
     * @param sizeNumber
     * @param sizeSpecialChar
     * @return AlphaNumeric  with special characters
     */
    public String getRandomAlphaNumericWithSpecialCharacter(int sizeString, int sizeNumber, int sizeSpecialChar) {

        int size = sizeNumber + sizeSpecialChar + sizeString;
        int stringCounter = 0;
        int numberCounter = 0;
        int specialCharCounter = 0;
        StringBuilder sb = new StringBuilder(size);


        for (int i = 0; i < size; i = i + 1) {
            char choice = INT_CHAR.charAt(rnd.nextInt(3));
            System.out.println(choice);
            switch (choice) {
                case '0':
                    if (stringCounter != sizeString) {
                        sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
                        stringCounter++;
                        break;
                    }
                case '1':
                    if (numberCounter != sizeNumber) {
                        sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
                        numberCounter++;
                        break;
                    }

                case '2':
                    if (specialCharCounter != sizeSpecialChar) {
                        sb.append(SPECIAL_CHAR.charAt(rnd.nextInt(SPECIAL_CHAR.length())));
                        specialCharCounter++;
                        break;
                    }
                default:
                    if (stringCounter != sizeString) {
                        sb.append(UPPER_CASE.charAt(rnd.nextInt(UPPER_CASE.length())));
                        stringCounter++;
                        break;
                    }
                    if (numberCounter != sizeNumber) {
                        sb.append(INT_CHAR.charAt(rnd.nextInt(INT_CHAR.length())));
                        numberCounter++;
                        break;
                    }
                    if (specialCharCounter != sizeSpecialChar) {
                        sb.append(SPECIAL_CHAR.charAt(rnd.nextInt(SPECIAL_CHAR.length())));
                        specialCharCounter++;
                        break;
                    }
            }
        }
        log.info("Returning random generated string" + sb.toString());
        return sb.toString();

    }

    public String getRandomEmail(int size) {
        return null;
    }

    /**
     * This method will return Random password with alpha numeric based on the size
     *
     * @param size of the password
     * @return string as password
     */
    public String getRandomPassword(int size) {
        log.info("Return alpha numeric password");
        return getRandomAlphaNumeric(size);
    }

    public String getRandomPassword(boolean string, boolean number, boolean specialChar, boolean capitalize, int size) {
        return null;
    }

    /**
     * This method will return an email address with time stamp.
     *
     * @return email with time stamp
     */
    public String getEmailWithTimeStamp() {
        String email = "";
        //	long now = Instant.now().toEpochMilli();
//	email= "TTN"+now+"yopmail.com";
        return email;
    }

    /**
     * This method will return a paragraph of given size.
     *
     * @param size
     * @return String with a requried paragraph
     */
    public String getParagraph(int size) {
        log.info("Getting souce string into variable");
        String source = sourceString();
        try {
            if (size <= source.length()) {
                return source.substring(0, size);
            } else
                return source;
        } catch (Exception e) {
            log.error("Error occured in getting string");
            log.error(e.getMessage());
            return null;
        }

    }

    /**
     * This method will use for getting source String this mehtod will work getParagraph method.
     *
     * @return a string that be use as source of the getParagraph method
     */

    public String sourceString() {
        String s = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut eros et nisl sagittis vestibulum. Nullam nulla eros, ultricies sit amet, nonummy id, imperdiet feugiat, pede. Sed lectus. Donec mollis hendrerit risus. Phasellus nec sem in justo pellentesque facilisis. Etiam imperdiet imperdiet orci. Nunc nec neque. Phasellus leo dolor, tempus non, auctor et, hendrerit quis, nisi. Curabitur ligula sapien, tincidunt non, euismod vitae, posuere imperdiet, leo. Maecenas malesuada. Praesent congue erat at massa. Sed cursus turpis vitae tortor. Donec posuere vulputate arcu. Phasellus accumsan cursus velit. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed aliquam, nisi quis porttitor congue, elit erat euismod orci, ac placerat dolor lectus quis orci. Phasellus consectetuer vestibulum elit. Aenean tellus metus, bibendum sed, posuere ac, mattis non, nunc. Vestibulum fringilla pede sit amet augue. In turpis. Pellentesque posuere. Praesent turpis. Aenean posuere, tortor sed cursus feugiat, nunc augue blandit nunc, eu sollicitudin urna dolor sagittis lacus. Donec elit libero, sodales nec, volutpat a, suscipit non, turpis. Nullam sagittis. Suspendisse pulvinar, augue ac venenatis condimentum, sem libero volutpat nibh, nec pellentesque velit pede quis nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Fusce id purus. Ut varius tincidunt libero. Phasellus dolor. Maecenas vestibulum mollis diam. Pellentesque ut neque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. In dui magna, posuere eget, vestibulum et, tempor auctor, justo. In ac felis quis tortor malesuada pretium. Pellentesque auctor neque nec urna. Proin sapien ipsum, porta a, auctor quis, euismod ut, mi. Aenean viverra rhoncus pede. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Ut non enim eleifend felis pretium feugiat. Vivamus quis mi. Phasellus a est. Phasellus magna. In hac habitasse platea dictumst. Curabitur at lacus ac velit ornare lobortis. Curabitur a felis in nunc fringilla tristique. Morbi mattis ullamcorper velit. Phasellus gravida semper nisi. Nullam vel sem. Pellentesque libero tortor, tincidunt et, tincidunt eget, semper nec, quam. Sed hendrerit. Morbi ac felis. Nunc egestas, augue at pellentesque laoreet, felis eros vehicula leo, at malesuada velit leo quis pede. Donec interdum, metus et hendrerit aliquet, dolor diam sagittis ligula, eget egestas libero turpis vel mi. Nunc nulla. Fusce risus nisl, viverra et, tempor et, pretium in, sapien. Donec venenatis vulputate lorem. Morbi nec metus. Phasellus blandit leo ut odio. Maecenas ullamcorper, dui et placerat feugiat, eros pede varius nisi, condimentum viverra felis nunc et lorem. Sed magna purus, fermentum eu, tincidunt eu, varius ut, felis. In auctor lobortis lacus. Quisque libero metus, condimentum nec, tempor a, commodo mollis, magna. Vestibulum ullamcorper mauris at ligula. Fusce fermentum. Nullam cursus lacinia erat. Praesent blandit laoreet nibh. Fusce convallis metus id felis luctus adipiscing. Pellentesque egestas, neque sit amet convallis pulvinar, justo nulla eleifend augue, ac auctor orci leo non est. Quisque id mi. Ut tincidunt tincidunt erat. Etiam feugiat lorem non metus. Vestibulum dapibus nunc ac augue. Curabitur vestibulum aliquam leo. Praesent egestas neque eu enim. In hac habitasse platea dictumst. Fusce a quam. Etiam ut purus mattis mauris sodales aliquam. Curabitur nisi. Quisque malesuada placerat nisl. Nam ipsum risus, rutrum vitae, vestibulum eu, molestie vel, lacus. Sed augue ipsum, egestas nec, vestibulum et, malesuada adipiscing, dui. Vestibulum facilisis, purus nec pulvinar iaculis, ligula mi congue nunc, vitae euismod ligula urna in dolor. Mauris sollicitudin fermentum libero. Praesent nonummy mi in odio. Nunc interdum lacus sit amet orci. Vestibulum rutrum, mi nec elementum vehicula, eros quam gravida nisl, id fringilla neque ante vel mi. Morbi mollis tellus ac sapien. Phasellus volutpat, metus eget egestas mollis, lacus lacus blandit dui, id egestas quam mauris ut lacus. Fusce vel dui. Sed in libero ut nibh placerat accumsan. Proin faucibus arcu quis ante. In consectetuer turpis ut velit. Nulla sit amet est. Praesent metus tellus, elementum eu, semper a, adipiscing nec, purus. Cras risus ipsum, faucibus ut, ullamcorper id, varius ac, leo. Suspendisse feugiat. Suspendisse enim turpis, dictum sed, iaculis a, condimentum nec, nisi. Praesent nec nisl a purus blandit viverra. Praesent ac massa at ligula laoreet iaculis. Nulla neque dolor, sagittis eget, iaculis quis, molestie non, velit. Mauris turpis nunc, blandit et, volutpat molestie, porta ut, ligula. Fusce pharetra convallis urna. Quisque ut nisi. Donec mi odio, faucibus at, scelerisque quis, convallis in, nisi. Suspendisse non nisl sit amet velit hendrerit rutrum. Ut leo. Ut a nisl id ante tempus hendrerit. Proin pretium, leo ac pellentesque mollis, felis nunc ultrices eros, sed gravida augue augue mollis justo. Suspendisse eu ligula. Nulla facilisi. Donec id justo. Praesent porttitor, nulla vitae posuere iaculis, arcu nisl dignissim dolor, a pretium mi sem ut ipsum. Curabitur suscipit suscipit tellus. Praesent vestibulum dapibus nibh. Etiam iaculis nunc ac metus. Ut id nisl quis enim dignissim sagittis. Etiam sollicitudin, ipsum eu pulvinar rutrum, tellus ipsum laoreet sapien, quis venenatis ante odio sit amet eros. Proin magna. Duis vel nibh at velit scelerisque suscipit. Curabitur turpis. Vestibulum suscipit nulla quis orci. Fusce ac felis sit amet ligula pharetra condimentum. Maecenas egestas arcu quis ligula mattis placerat. Duis lobortis massa imperdiet quam. Suspendisse potenti. Pellentesque commodo eros a enim. Vestibulum turpis sem, aliquet eget, lobortis pellentesque, rutrum eu, nisl. Sed libero. Aliquam erat volutpat. Etiam vitae tortor. Morbi vestibulum volutpat enim. Aliquam eu nunc. Nunc sed turpis. Sed mollis, eros et ultrices tempus, mauris ipsum aliquam libero, non adipiscing dolor urna a orci. Nulla porta dolor. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Pellentesque dapibus hendrerit tortor. Praesent egestas tristique nibh. Sed a libero. Cras varius. Donec vitae orci sed dolor rutrum auctor. Fusce egestas elit eget lorem. Suspendisse nisl elit, rhoncus eget, elementum ac, condimentum eget, diam. Nam at tortor in tellus interdum sagittis. Aliquam lobortis. Donec orci lectus, aliquam ut, faucibus non, euismod id, nulla. Curabitur blandit mollis lacus. Nam adipiscing. Vestibulum eu odio. Vivamus laoreet. Nullam tincidunt adipiscing enim. Phasellus tempus. Proin viverra, ligula sit amet ultrices semper, ligula arcu tristique sapien, a accumsan nisi mauris ac eros. Fusce neque. Suspendisse faucibus, nunc et pellentesque egestas, lacus ante convallis tellus, vitae iaculis lacus elit id tortor. Vivamus aliquet elit ac nisl. Fusce fermentum odio nec arcu. Vivamus euismod mauris. In ut quam vitae odio lacinia tincidunt. Praesent ut ligula non mi varius sagittis. Cras sagittis. Praesent ac sem eget est egestas volutpat. Vivamus consectetuer hendrerit lacus. Cras non dolor. Vivamus in erat ut urna cursus vestibulum. Fusce commodo aliquam arcu. Nam commodo suscipit quam. Quisque id odio. Praesent venenatis metus at tortor pulvinar varius. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vita";
        return s;
    }
}
	

	

