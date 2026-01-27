package club.p6e.coat.shield;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Signature {


    Parameter validate(HttpServletRequest request, HttpServletResponse response);

}
