package lotto;

import camp.nextstep.edu.missionutils.test.NsTest;
import lotto.handler.ExceptionHandler;
import lotto.message.info.InfoMessage;
import lotto.model.domain.Lotto;
import lotto.model.service.LottServiceImpl;
import lotto.model.service.LottoService;
import lotto.view.inputview.InputView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Map;

import static camp.nextstep.edu.missionutils.test.Assertions.assertRandomUniqueNumbersInRangeTest;
import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest extends NsTest {
    private static final String ERROR_MESSAGE = "[ERROR]";
    private final InputView inputView = new InputView(new ExceptionHandler(), new LottServiceImpl());
    private final LottoService lottoService = new LottServiceImpl();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void 기능_테스트() {
        assertRandomUniqueNumbersInRangeTest(
                () -> {
                    run("8000", "1,2,3,4,5,6", "7");
                    assertThat(output()).contains(
                            "8개를 구매했습니다.",
                            "[8, 21, 23, 41, 42, 43]",
                            "[3, 5, 11, 16, 32, 38]",
                            "[7, 11, 16, 35, 36, 44]",
                            "[1, 8, 11, 31, 41, 42]",
                            "[13, 14, 16, 38, 42, 45]",
                            "[7, 11, 30, 40, 42, 43]",
                            "[2, 13, 22, 32, 38, 45]",
                            "[1, 3, 5, 14, 22, 45]",
                            "3개 일치 (5,000원) - 1개",
                            "4개 일치 (50,000원) - 0개",
                            "5개 일치 (1,500,000원) - 0개",
                            "5개 일치, 보너스 볼 일치 (30,000,000원) - 0개",
                            "6개 일치 (2,000,000,000원) - 0개",
                            "총 수익률은 62.5%입니다."
                    );
                },
                List.of(8, 21, 23, 41, 42, 43),
                List.of(3, 5, 11, 16, 32, 38),
                List.of(7, 11, 16, 35, 36, 44),
                List.of(1, 8, 11, 31, 41, 42),
                List.of(13, 14, 16, 38, 42, 45),
                List.of(7, 11, 30, 40, 42, 43),
                List.of(2, 13, 22, 32, 38, 45),
                List.of(1, 3, 5, 14, 22, 45)
        );
    }

    @Test
    void 예외_테스트() {
        assertSimpleTest(() -> {
            runException("1000j");
            assertThat(output()).contains(ERROR_MESSAGE);
        });
    }

    @Test
    @DisplayName("로또 구입 금액이 1,000원 단위가 아니라면 예외처리 하는 테스트")
    public void purchaseAmountExceptionTest() {
        //given
        int amount = 1313;
        //when, then
        assertThrows(IllegalArgumentException.class,
                () -> exceptionHandler.validatePurchaseAmount(amount));
    }

    @Test
    @DisplayName("1,000원 단위로 잘 입력했는지 확인하는 테스트")
    public void purchaseAmountRightInputTest() {
        //given
        int amount = 2000;
        //when, then
        assertDoesNotThrow(() -> exceptionHandler.validatePurchaseAmount(amount));
    }

    @Test
    @DisplayName("구입 금액에 따른 로또 구매개수 계산하는 테스트")
    public void calculateLottoCnt() {
        //given
        int amount = 5000;
        int resultCnt = 5;
        //when, then
        assertEquals(resultCnt, lottoService.calculateLottoCount(amount));

    }

    @Test
    @DisplayName("구입한 당첨번호 중 1부터 45 사이가 아닌 값이 존재하면 예외처리하는 테스트")
    public void requestWinningNumbersExceptionTest() {
        //given
        List<Integer> list = List.of(1, 48, 2, 3, 4, 5);
        //when, then
        assertThrows(IllegalArgumentException.class,
                () -> exceptionHandler.validateWinningNumbers(list));
    }

    @Test
    @DisplayName("당첨번호의 개수가 6개가 아닐 경우 예외처리 하는 테스트")
    public void winningNumbersCountNotSixExceptionTest() {
        //given
        String str = "1,2,3,4,5,6,7";
        List<Integer> winningNumbers = lottoService.extractWinningNumbersFromString(str);
        //when, then
        assertThrows(IllegalArgumentException.class,
                () -> new Lotto(winningNumbers));
    }

    @Test
    @DisplayName("보너스 번호의 입력값이 1과 45 사이가 아니면 예외처리하는 테스트")
    public void inputBonusNumberExceptionTest() {
        //given
        int bonusNumber = 46;
        //when, then
        assertThrows(IllegalArgumentException.class,
                () -> exceptionHandler.validateBonusNumber(bonusNumber));
    }

    @Test
    @DisplayName("당첨통계가 맞는지 검증하는 테스트 입니다.")
    public void testCalculateWinningStatistics() {
        //given
        List<List<Integer>> purchasedLotto = List.of(
                List.of(8, 21, 23, 41, 42, 43),
                List.of(3, 5, 11, 16, 32, 38),
                List.of(7, 11, 16, 35, 36, 44),
                List.of(1, 8, 11, 31, 41, 42),
                List.of(13, 14, 16, 38, 42, 45),
                List.of(7, 11, 30, 40, 42, 43),
                List.of(2, 13, 22, 32, 38, 45),
                List.of(1, 3, 5, 14, 22, 45)
        );
        List<Integer> winningNumbers = List.of(1, 2, 3, 4, 5, 6);
        int bonusNumber = 7;

        //when
        Map<String, Integer> matchCounts = lottoService.calculateWinningStatistics(purchasedLotto, winningNumbers, bonusNumber);
        //then
        assertEquals(0, matchCounts.get(InfoMessage.FIRST.getMessage()));
        assertEquals(0, matchCounts.get(InfoMessage.SECOND.getMessage()));
        assertEquals(0, matchCounts.get(InfoMessage.THIRD.getMessage()));
        assertEquals(0, matchCounts.get(InfoMessage.FOURTH.getMessage()));
        assertEquals(1, matchCounts.get(InfoMessage.FIFTH.getMessage()));

    }

    @Test
    @DisplayName("당첨된 로또 통계를 기반으로 얼마를 이득봤는지 계산하는 테스트")
    public void calculateTotalPrizeTest() {
        //given
        List<List<Integer>> purchasedLotto = List.of(
                List.of(8, 21, 23, 41, 42, 43),
                List.of(3, 5, 11, 16, 32, 38),
                List.of(7, 11, 16, 35, 36, 44),
                List.of(1, 8, 11, 31, 41, 42),
                List.of(13, 14, 16, 38, 42, 45),
                List.of(7, 11, 30, 40, 42, 43),
                List.of(2, 13, 22, 32, 38, 45),
                List.of(1, 3, 5, 14, 22, 45)
        );
        List<Integer> winningNumbers = List.of(1, 2, 3, 4, 5, 6);
        int bonusNumber = 7;

        //when
        Map<String, Integer> matchCounts = lottoService.calculateWinningStatistics(purchasedLotto, winningNumbers, bonusNumber);
        Long calculateTotalPrize = lottoService.calculateTotalPrize(matchCounts);
        //then
        assertEquals(calculateTotalPrize, 5000);
    }

    @Test
    @DisplayName("당첨된 로또 통계를 기반으로 수익률을 계산하는 테스트")
    public void calculateYieldTest() {
        //given
        int purchaseAmount = 8000;
        List<List<Integer>> purchasedLotto = List.of(
                List.of(8, 21, 23, 41, 42, 43),
                List.of(3, 5, 11, 16, 32, 38),
                List.of(7, 11, 16, 35, 36, 44),
                List.of(1, 8, 11, 31, 41, 42),
                List.of(13, 14, 16, 38, 42, 45),
                List.of(7, 11, 30, 40, 42, 43),
                List.of(2, 13, 22, 32, 38, 45),
                List.of(1, 3, 5, 14, 22, 45)
        );
        List<Integer> winningNumbers = List.of(1, 2, 3, 4, 5, 6);
        int bonusNumber = 7;

        //when
        Map<String, Integer> matchCounts = lottoService.calculateWinningStatistics(purchasedLotto, winningNumbers, bonusNumber);
        Long calculateTotalPrize = lottoService.calculateTotalPrize(matchCounts);
        double yield = lottoService.calculateYield(calculateTotalPrize, purchaseAmount);
        //then
        assertEquals(yield, 62.5);
    }


    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}
