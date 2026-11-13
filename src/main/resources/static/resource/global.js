// 모든 페이지에서 사용하는 공통 스크립트 (14168 42~44강)
document.addEventListener("DOMContentLoaded", () => {
    // <select value="B"> 처럼 value 속성으로 기본 선택값을 지정할 수 있게 한다.
    document.querySelectorAll("select[value]").forEach((select) => {
        const value = select.getAttribute("value");
        if (value) select.value = value;
    });

    // <a method="DELETE" onclick="return confirm('...')"> 처럼 쓰면
    // 링크 클릭 시 onclick이 먼저 실행되도록 data-onclick으로 옮겨 둔다.
    document.querySelectorAll("a[method][onclick]").forEach((anchor) => {
        const onclick = anchor.getAttribute("onclick");
        if (onclick) {
            anchor.removeAttribute("onclick");
            anchor.setAttribute("data-onclick", onclick);
        }
    });

    // <a method="DELETE|PUT|POST" href="..."> 를 클릭하면
    // 숨은 form을 만들어 POST로 전송하고, _method 파라미터로 실제 메서드를 알려 준다.
    // (HTML의 링크와 폼은 GET, POST만 보낼 수 있기 때문)
    document.querySelectorAll("a[method]").forEach((anchor) => {
        anchor.addEventListener("click", (e) => {
            e.preventDefault(); // 기본 링크 이동(GET) 방지

            const onclickAfterCode = anchor.getAttribute("data-onclick");
            if (onclickAfterCode) {
                const onclickAfter = new Function(onclickAfterCode);
                if (!onclickAfter()) return false; // confirm에서 '취소'를 누르면 중단
            }

            const url = new URL(anchor.getAttribute("href"), window.location.origin);
            const method = anchor.getAttribute("method").toUpperCase();

            const form = document.createElement("form");
            form.action = url.pathname;
            form.method = "POST";

            const addHidden = (name, value) => {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = name;
                input.value = value;
                form.appendChild(input);
            };

            // CSRF 토큰 (layout.html의 <meta name="_csrf">)
            addHidden("_csrf", document.querySelector('meta[name="_csrf"]')?.getAttribute("content") || "");
            // 실제 HTTP 메서드 (spring.mvc.hiddenmethod.filter.enabled=true 필요)
            if (method !== "POST") addHidden("_method", method);
            // 쿼리 파라미터 -> hidden input
            for (const [key, value] of url.searchParams.entries()) addHidden(key, value);

            document.body.appendChild(form);
            form.submit();
        });
    });
});

// 댓글 작성 폼 클라이언트 검증 (14168 35강). 서버에서도 반드시 다시 검증한다.
function Comment__submitWriteForm(form) {
    form.content.value = form.content.value.trim();

    if (form.content.value.length === 0) {
        alert("댓글 내용을 입력해주세요.");
        form.content.focus();
        return;
    }

    form.submit();
}
